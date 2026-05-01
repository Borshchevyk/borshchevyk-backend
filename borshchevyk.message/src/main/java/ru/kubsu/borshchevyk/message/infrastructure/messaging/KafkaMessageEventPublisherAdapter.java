package ru.kubsu.borshchevyk.message.infrastructure.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.message.application.port.out.MessageEventPublisherPort;
import ru.kubsu.borshchevyk.message.domain.event.MessageCreatedEvent;
import ru.kubsu.borshchevyk.message.domain.event.MessageDeletedEvent;
import ru.kubsu.borshchevyk.message.domain.model.message.Message;
import ru.kubsu.borshchevyk.message.domain.exception.MessagingSerializationException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Aleksey Timko
 * @since 2026-05-01
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaMessageEventPublisherAdapter implements MessageEventPublisherPort {

    private static final String TOPIC = "messages.events";
    private static final String TOPIC_DELETED = "messages.deleted.events";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void publishMessageCreatedEvent(Message message, List<String> targetUserIds) {
        MessageCreatedEvent event = new MessageCreatedEvent(
                message.getId() != null ? message.getId().value() : null,
                message.getChatId() != null ? message.getChatId().value() : null,
                message.getAuthorId() != null ? message.getAuthorId().value() : null,
                message.getText(),
                message.getCreatedAt(),
                message.getStatus(),
                targetUserIds,
                message.getAttachments() != null ? message.getAttachments().stream()
                        .map(a -> new MessageCreatedEvent.AttachmentInfo(
                                a.getId(), 
                                a.getType(),
                                a.getOriginalFilename(),
                                a.getExtension(),
                                a.getSizeBytes(),
                                a.getDuration()
                        ))
                        .collect(Collectors.toList()) : null
        );

        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(TOPIC, message.getId().value().toString(), payload);
            log.info("Published MessageCreatedEvent to topic {}: {}", TOPIC, payload);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize MessageCreatedEvent", e);
            throw new MessagingSerializationException("Failed to serialize event", e);
        }
    }

    @Override
    public void publishMessageDeletedEvent(Message message, List<String> targetUserIds) {
        MessageDeletedEvent event = new MessageDeletedEvent(
                message.getId() != null ? message.getId().value() : null,
                message.getChatId() != null ? message.getChatId().value() : null,
                targetUserIds
        );

        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(TOPIC_DELETED, message.getId().value().toString(), payload);
            log.info("Published MessageDeletedEvent to topic {}: {}", TOPIC_DELETED, payload);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize MessageDeletedEvent", e);
            throw new MessagingSerializationException("Failed to serialize event", e);
        }
    }
}
