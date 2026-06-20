package ru.kubsu.borshchevyk.sync.infrastructure.adapter.in.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.sync.application.dto.command.ProcessDomainEventCommand;
import ru.kubsu.borshchevyk.sync.application.port.in.ProcessDomainEventUseCase;
import ru.kubsu.borshchevyk.sync.domain.model.EventType;
import ru.kubsu.borshchevyk.sync.infrastructure.adapter.in.messaging.dto.MessageCreatedEvent;
import ru.kubsu.borshchevyk.sync.infrastructure.adapter.in.messaging.dto.UserRegisteredEvent;

import java.util.UUID;

/**
 * Adapter for consuming events from Kafka across all microservices.
 *
 * @author Aleksey Timko
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaEventConsumerAdapter {

    private final ProcessDomainEventUseCase processDomainEventUseCase;
    private final ObjectMapper objectMapper;

    // --- USER DOMAIN ---

    @KafkaListener(topics = "${app.kafka.topics.user-registered}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeUserRegistered(String payload) {
        log.info("Received UserRegisteredEvent payload");
        try {
            UserRegisteredEvent event = objectMapper.readValue(payload, UserRegisteredEvent.class);
            process(event.userId(), event.userId(), EventType.USER_REGISTERED, payload);
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize UserRegisteredEvent", e);
        }
    }

    @KafkaListener(topics = "${app.kafka.topics.user-updated}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeUserUpdated(String payload) {
        log.info("Received UserUpdatedEvent payload");
        extractIdAndProcess(payload, "userId", EventType.USER_UPDATED);
    }

    @KafkaListener(topics = "${app.kafka.topics.user-deleted}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeUserDeleted(String payload) {
        log.info("Received UserDeletedEvent payload");
        extractIdAndProcess(payload, "userId", EventType.USER_DELETED);
    }

    // --- MESSAGE DOMAIN ---

    @KafkaListener(topics = "${app.kafka.topics.message-created}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeMessageCreated(String payload) {
        log.info("Received MessageCreatedEvent payload");
        try {
            MessageCreatedEvent event = objectMapper.readValue(payload, MessageCreatedEvent.class);
            if (event.targetUserIds() != null && !event.targetUserIds().isEmpty()) {
                String messageId = event.id() != null ? event.id() : UUID.randomUUID().toString();
                for (String targetUserId : event.targetUserIds()) {
                    process(messageId, targetUserId, EventType.MESSAGE_CREATED, payload);
                }
            } else {
                JsonNode root = objectMapper.readTree(payload);
                String messageId = root.has("messageId") ? root.get("messageId").asText() : UUID.randomUUID().toString();
                process(messageId, null, EventType.MESSAGE_CREATED, payload);
            }
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize MessageCreatedEvent", e);
        }
    }

    @KafkaListener(topics = "${app.kafka.topics.message-deleted}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeMessageDeleted(String payload) {
        log.info("Received MessageDeletedEvent payload");
        extractIdAndProcess(payload, "messageId", EventType.MESSAGE_DELETED);
    }

    // --- CHAT DOMAIN ---

    @KafkaListener(topics = "${app.kafka.topics.chat-events}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeChatEvent(String payload) {
        log.info("Received ChatEvent payload");
        try {
            JsonNode root = objectMapper.readTree(payload);
            String action = root.has("action") ? root.get("action").asText() : "";
            String userId = root.has("userId") ? root.get("userId").asText() : null;
            
            EventType eventType = switch (action) {
                case "JOINED" -> EventType.MEMBER_ADDED;
                case "KICKED", "LEFT" -> EventType.MEMBER_REMOVED;
                case "PERMISSIONS_UPDATED" -> EventType.MEMBER_UPDATED;
                case "CREATED" -> EventType.CHAT_CREATED;
                case "DELETED" -> EventType.CHAT_DELETED;
                case "READ" -> EventType.MESSAGE_READ;
                default -> EventType.CHAT_UPDATED;
            };
            
            String entityId = root.has("chatId") ? root.get("chatId").asText() : UUID.randomUUID().toString();
            process(entityId, userId, eventType, payload);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse ChatEvent payload", e);
        }
    }

    // --- CALL DOMAIN ---

    @KafkaListener(topics = "${app.kafka.topics.call-events}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeCallEvent(String payload) {
        log.info("Received CallEvent payload");
        extractIdAndProcess(payload, "callId", EventType.CALL_EVENT);
    }

    // --- HELPER METHODS ---

    private void extractIdAndProcess(String payload, String idFieldName, EventType eventType) {
        try {
            JsonNode root = objectMapper.readTree(payload);
            if (root.has(idFieldName)) {
                String id = root.get(idFieldName).asText();
                String userId = null;
                if (idFieldName.equals("userId")) userId = id; // Fallback for user events
                process(id, userId, eventType, payload);
            } else {
                log.warn("Payload missing {} for event type {}: {}", idFieldName, eventType, payload);
                process(UUID.randomUUID().toString(), null, eventType, payload);
            }
        } catch (JsonProcessingException e) {
            log.error("Failed to parse JSON payload for event {}", eventType, e);
        }
    }

    private void process(String entityIdStr, String userIdStr, EventType type, String payload) {
        try {
            UUID entityId = UUID.fromString(entityIdStr);
            UUID userId = userIdStr != null ? UUID.fromString(userIdStr) : null;
            processDomainEventUseCase.process(new ProcessDomainEventCommand(entityId, userId, type, payload));
        } catch (IllegalArgumentException e) {
            log.error("Invalid UUID format for entityId or userId: {} / {}", entityIdStr, userIdStr, e);
        }
    }
}
