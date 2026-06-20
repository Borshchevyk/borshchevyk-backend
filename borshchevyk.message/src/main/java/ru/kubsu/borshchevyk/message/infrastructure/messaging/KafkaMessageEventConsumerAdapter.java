package ru.kubsu.borshchevyk.message.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.kubsu.borshchevyk.message.application.port.out.LoadMessagePort;
import ru.kubsu.borshchevyk.message.application.port.out.NotifyUserPort;
import ru.kubsu.borshchevyk.message.domain.event.message.MessageCreatedEvent;
import ru.kubsu.borshchevyk.message.domain.event.message.MessageDeletedEvent;
import ru.kubsu.borshchevyk.message.domain.model.value.MessageId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaMessageEventConsumerAdapter {

    private final LoadMessagePort loadMessagePort;
    private final NotifyUserPort notifyUserPort;

    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    @KafkaListener(topics = "messages.events", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeMessageCreatedEvent(String payload) {
        log.debug("Received MessageCreatedEvent payload: {}", payload);
        try {
            MessageCreatedEvent event = objectMapper.readValue(payload, MessageCreatedEvent.class);
            if (event.id() != null && event.targetUserIds() != null && !event.targetUserIds().isEmpty()) {
                loadMessagePort.findById(new MessageId(event.id())).ifPresent(message -> {
                    for (String targetId : event.targetUserIds()) {
                        notifyUserPort.notifyUser(new UserId(UUID.fromString(targetId)), message);
                    }
                });
            }
        } catch (Exception e) {
            log.error("Error processing MessageCreatedEvent: {}", e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    @KafkaListener(topics = "messages.deleted.events", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeMessageDeletedEvent(String payload) {
        log.debug("Received MessageDeletedEvent payload: {}", payload);
        try {
            MessageDeletedEvent event = objectMapper.readValue(payload, MessageDeletedEvent.class);
            if (event.messageId() != null && event.targetUserIds() != null && !event.targetUserIds().isEmpty()) {
                loadMessagePort.findById(new MessageId(event.messageId())).ifPresent(message -> {
                    for (String targetId : event.targetUserIds()) {
                        notifyUserPort.notifyUser(new UserId(UUID.fromString(targetId)), message);
                    }
                });
            }
        } catch (Exception e) {
            log.error("Error processing MessageDeletedEvent: {}", e.getMessage(), e);
        }
    }
}