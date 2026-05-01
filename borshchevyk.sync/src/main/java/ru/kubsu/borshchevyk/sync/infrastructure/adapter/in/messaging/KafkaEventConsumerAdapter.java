package ru.kubsu.borshchevyk.sync.infrastructure.adapter.in.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.sync.application.port.in.ProcessIncomingEventUseCase;
import ru.kubsu.borshchevyk.sync.domain.model.EventType;
import ru.kubsu.borshchevyk.sync.infrastructure.adapter.in.messaging.dto.MessageCreatedEvent;
import ru.kubsu.borshchevyk.sync.infrastructure.adapter.in.messaging.dto.UserRegisteredEvent;

import java.util.UUID;

/**
 * Adapter for consuming events from Kafka.
 *
 * @author Aleksey Timko
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaEventConsumerAdapter {

    private final ProcessIncomingEventUseCase processIncomingEventUseCase;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${app.kafka.topics.user-registered}")
    public void consumeUserRegistered(String payload) {
        log.info("Received UserRegisteredEvent payload");
        try {
            UserRegisteredEvent event = objectMapper.readValue(payload, UserRegisteredEvent.class);
            processIncomingEventUseCase.process(
                    UUID.fromString(event.userId()),
                    EventType.USER_REGISTERED,
                    payload
            );
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize UserRegisteredEvent", e);
        }
    }

    @KafkaListener(topics = "${app.kafka.topics.message-created}")
    public void consumeMessageCreated(String payload) {
        log.info("Received MessageCreatedEvent payload");
        try {
            MessageCreatedEvent event = objectMapper.readValue(payload, MessageCreatedEvent.class);
            if (event.targetUserIds() != null) {
                for (String targetId : event.targetUserIds()) {
                    processIncomingEventUseCase.process(
                            UUID.fromString(targetId),
                            EventType.MESSAGE_CREATED,
                            payload
                    );
                }
            }
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize MessageCreatedEvent", e);
        }
    }
}
