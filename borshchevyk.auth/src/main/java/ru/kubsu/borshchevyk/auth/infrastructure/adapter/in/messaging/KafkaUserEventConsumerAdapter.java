package ru.kubsu.borshchevyk.auth.infrastructure.adapter.in.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.auth.application.port.in.SyncAccountUseCase;
import ru.kubsu.borshchevyk.auth.domain.event.UserDeletedEvent;
import ru.kubsu.borshchevyk.auth.domain.event.UserUpdatedEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaUserEventConsumerAdapter {

    private final SyncAccountUseCase syncAccountUseCase;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${app.kafka.topics.user-updated:user-updated-events}", groupId = "${spring.kafka.consumer.group-id:auth-service-group}")
    public void consumeUpdated(String payload) {
        try {
            UserUpdatedEvent event = objectMapper.readValue(payload, UserUpdatedEvent.class);
            syncAccountUseCase.syncUpdated(event);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse UserUpdatedEvent payload: {}", payload, e);
        } catch (Exception e) {
            log.error("Error processing UserUpdatedEvent", e);
        }
    }

    @KafkaListener(topics = "${app.kafka.topics.user-deleted:user-deleted-events}", groupId = "${spring.kafka.consumer.group-id:auth-service-group}")
    public void consumeDeleted(String payload) {
        try {
            UserDeletedEvent event = objectMapper.readValue(payload, UserDeletedEvent.class);
            syncAccountUseCase.syncDeleted(event);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse UserDeletedEvent payload: {}", payload, e);
        } catch (Exception e) {
            log.error("Error processing UserDeletedEvent", e);
        }
    }
}
