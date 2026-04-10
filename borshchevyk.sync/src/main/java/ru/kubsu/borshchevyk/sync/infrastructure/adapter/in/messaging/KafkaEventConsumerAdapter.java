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

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaEventConsumerAdapter {

    private final ProcessIncomingEventUseCase processIncomingEventUseCase;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${app.kafka.topics.user-registered}")
    public void consumeUserRegistered(UserRegisteredEvent event) {
        log.info("Received UserRegisteredEvent: {}", event);
        try {
            String payload = objectMapper.writeValueAsString(event);
            processIncomingEventUseCase.process(
                    UUID.fromString(event.getUserId()),
                    EventType.USER_REGISTERED,
                    payload
            );
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize UserRegisteredEvent", e);
        }
    }

    @KafkaListener(topics = "${app.kafka.topics.message-created}")
    public void consumeMessageCreated(MessageCreatedEvent event) {
        log.info("Received MessageCreatedEvent: {}", event);
        try {
            String payload = objectMapper.writeValueAsString(event);
            if (event.getTargetUserIds() != null) {
                for (String targetId : event.getTargetUserIds()) {
                    processIncomingEventUseCase.process(
                            UUID.fromString(targetId),
                            EventType.MESSAGE_CREATED,
                            payload
                    );
                }
            }
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize MessageCreatedEvent", e);
        }
    }
}
