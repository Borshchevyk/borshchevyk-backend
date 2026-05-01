package ru.kubsu.borshchevyk.message.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.message.application.port.out.MessagePort;
import ru.kubsu.borshchevyk.message.application.port.out.RealtimeNotificationPort;
import ru.kubsu.borshchevyk.message.domain.event.MessageCreatedEvent;
import ru.kubsu.borshchevyk.message.domain.event.MessageDeletedEvent;
import ru.kubsu.borshchevyk.message.domain.model.message.Message;
import ru.kubsu.borshchevyk.message.domain.model.value.MessageId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;

import java.util.UUID;

/**
 * Consumes message events from Kafka and proxies them to the realtime notification port.
 *
 * @author Aleksey Timko
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaMessageEventConsumerAdapter {

    private final ObjectMapper objectMapper;
    private final MessagePort messagePort;
    private final RealtimeNotificationPort realtimeNotificationPort;

    @KafkaListener(topics = "messages.events", groupId = "${spring.kafka.consumer.group-id:message-service-group}")
    public void consumeMessageCreatedEvent(String payload) {
        log.debug("Received MessageCreatedEvent payload: {}", payload);
        try {
            MessageCreatedEvent event = objectMapper.readValue(payload, MessageCreatedEvent.class);
            if (event.id() != null && event.targetUserIds() != null && !event.targetUserIds().isEmpty()) {
                messagePort.findById(new MessageId(event.id())).ifPresent(message -> {
                    for (String targetId : event.targetUserIds()) {
                        realtimeNotificationPort.notifyUser(new UserId(UUID.fromString(targetId)), message);
                    }
                });
            }
        } catch (Exception e) {
            log.error("Error processing MessageCreatedEvent: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "messages.deleted.events", groupId = "${spring.kafka.consumer.group-id:message-service-group}")
    public void consumeMessageDeletedEvent(String payload) {
        log.debug("Received MessageDeletedEvent payload: {}", payload);
        try {
            MessageDeletedEvent event = objectMapper.readValue(payload, MessageDeletedEvent.class);
            if (event.messageId() != null && event.targetUserIds() != null && !event.targetUserIds().isEmpty()) {
                messagePort.findById(new MessageId(event.messageId())).ifPresent(message -> {
                    for (String targetId : event.targetUserIds()) {
                        realtimeNotificationPort.notifyUser(new UserId(UUID.fromString(targetId)), message);
                    }
                });
            }
        } catch (Exception e) {
            log.error("Error processing MessageDeletedEvent: {}", e.getMessage(), e);
        }
    }
}
