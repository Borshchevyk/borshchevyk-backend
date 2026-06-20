package ru.kubsu.borshchevyk.message.infrastructure.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import ru.kubsu.borshchevyk.message.application.port.out.PublishMessageDeletedEventPort;
import ru.kubsu.borshchevyk.message.domain.event.message.MessageDeletedEvent;
import ru.kubsu.borshchevyk.message.domain.exception.MessagingSerializationException;
import ru.kubsu.borshchevyk.message.domain.model.message.Message;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PublishMessageDeletedEventAdapter implements PublishMessageDeletedEventPort {

    private static final String TOPIC_DELETED = "messages.deleted.events";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void publishMessageDeletedEvent(Message message, List<String> targetUserIds) {
        MessageDeletedEvent event = new MessageDeletedEvent(
                message.getId() != null ? message.getId().value() : null,
                message.getChatId() != null ? message.getChatId().value() : null,
                targetUserIds
        );

        Runnable publishAction = () -> {
            try {
                String payload = objectMapper.writeValueAsString(event);
                kafkaTemplate.send(TOPIC_DELETED, message.getId().value().toString(), payload);
                log.info("Published MessageDeletedEvent to topic {}: {}", TOPIC_DELETED, payload);
            } catch (JsonProcessingException e) {
                log.error("Failed to serialize MessageDeletedEvent", e);
                throw new MessagingSerializationException("Failed to serialize event", e);
            }
        };

        executeAfterCommit(publishAction);
    }

    private void executeAfterCommit(Runnable action) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    action.run();
                }
            });
        } else {
            action.run();
        }
    }
}