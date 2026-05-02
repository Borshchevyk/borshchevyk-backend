package ru.kubsu.borshchevyk.user.infrastructure.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.user.application.port.out.UserEventPublisherPort;
import ru.kubsu.borshchevyk.user.domain.event.UserDeletedEvent;
import ru.kubsu.borshchevyk.user.domain.event.UserUpdatedEvent;

/**
 * Adapter for publishing user domain events to Kafka topics.
 *
 * @author Aleksey Timko
 * @since 2026-03-15
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaUserEventPublisherAdapter implements UserEventPublisherPort {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.kafka.topics.user-updated}")
    private String userUpdatedTopic;

    @Value("${app.kafka.topics.user-deleted}")
    private String userDeletedTopic;

    /**
     * Publishes a user updated event to Kafka.
     *
     * @param event the user updated event to publish
     */
    @Override
    public void publishUpdated(UserUpdatedEvent event) {
        log.debug("Publishing UserUpdatedEvent for user ID: {}", event.userId());
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(userUpdatedTopic, event.userId().toString(), payload);
            log.info("Published UserUpdatedEvent to Kafka topic: {} for user ID: {}", userUpdatedTopic, event.userId());
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize UserUpdatedEvent for user ID: {}", event.userId(), e);
        }
    }

    /**
     * Publishes a user deleted event to Kafka.
     *
     * @param event the user deleted event to publish
     */
    @Override
    public void publishDeleted(UserDeletedEvent event) {
        log.debug("Publishing UserDeletedEvent for user ID: {}", event.userId());
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(userDeletedTopic, event.userId().toString(), payload);
            log.info("Published UserDeletedEvent to Kafka topic: {} for user ID: {}", userDeletedTopic, event.userId());
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize UserDeletedEvent for user ID: {}", event.userId(), e);
        }
    }
}
