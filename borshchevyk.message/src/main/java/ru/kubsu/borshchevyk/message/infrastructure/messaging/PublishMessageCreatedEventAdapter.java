package ru.kubsu.borshchevyk.message.infrastructure.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import ru.kubsu.borshchevyk.message.application.port.out.PublishMessageCreatedEventPort;
import ru.kubsu.borshchevyk.message.domain.event.message.MessageCreatedEvent;
import ru.kubsu.borshchevyk.message.domain.exception.MessagingSerializationException;
import ru.kubsu.borshchevyk.message.domain.model.message.Message;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class PublishMessageCreatedEventAdapter implements PublishMessageCreatedEventPort {

    private static final String TOPIC = "messages.events";

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

        Runnable publishAction = () -> {
            try {
                String payload = objectMapper.writeValueAsString(event);
                kafkaTemplate.send(TOPIC, message.getId().value().toString(), payload);
                log.info("Published MessageCreatedEvent to topic {}: {}", TOPIC, payload);
            } catch (JsonProcessingException e) {
                log.error("Failed to serialize MessageCreatedEvent", e);
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