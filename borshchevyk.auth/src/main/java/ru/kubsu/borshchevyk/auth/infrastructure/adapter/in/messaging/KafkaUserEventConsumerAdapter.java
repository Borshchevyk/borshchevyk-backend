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

/**
 * Adapter for consuming user-related events from Kafka.
 *
 * @author Aleksey Timko
 * @since 2026-03-14
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaUserEventConsumerAdapter {

    private final SyncAccountUseCase syncAccountUseCase;
    private final ObjectMapper objectMapper;

    /**
     * Consumes user updated events.
     *
     * @param payload the JSON payload of the event
     */
    @KafkaListener(topics = "${app.kafka.topics.user-updated}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeUpdated(String payload) {
        log.info("Received UserUpdatedEvent: {}", payload);
        try {
            UserUpdatedEvent event = objectMapper.readValue(payload, UserUpdatedEvent.class);
            syncAccountUseCase.syncUpdated(event);
            log.info("UserUpdatedEvent processed successfully for user: {}", event.userId());
        } catch (JsonProcessingException e) {
            log.error("Failed to parse UserUpdatedEvent payload: {}", payload, e);
        } catch (Exception e) {
            log.error("Error processing UserUpdatedEvent", e);
        }
    }

    /**
     * Consumes user deleted events.
     *
     * @param payload the JSON payload of the event
     */
    @KafkaListener(topics = "${app.kafka.topics.user-deleted}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeDeleted(String payload) {
        log.info("Received UserDeletedEvent: {}", payload);
        try {
            UserDeletedEvent event = objectMapper.readValue(payload, UserDeletedEvent.class);
            syncAccountUseCase.syncDeleted(event);
            log.info("UserDeletedEvent processed successfully for user: {}", event.userId());
        } catch (JsonProcessingException e) {
            log.error("Failed to parse UserDeletedEvent payload: {}", payload, e);
        } catch (Exception e) {
            log.error("Error processing UserDeletedEvent", e);
        }
    }
}
