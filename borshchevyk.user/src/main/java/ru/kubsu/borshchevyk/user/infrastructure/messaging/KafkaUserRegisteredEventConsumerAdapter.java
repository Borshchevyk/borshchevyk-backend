package ru.kubsu.borshchevyk.user.infrastructure.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.user.application.port.in.CreateUserUseCase;
import ru.kubsu.borshchevyk.user.domain.event.UserRegisteredEvent;

/**
 * Adapter for consuming user registration events from Kafka.
 * This adapter listens for user registration events and triggers user creation in the local database.
 *
 * @author Aleksey Timko
 * @since 2026-03-15
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaUserRegisteredEventConsumerAdapter {

    private final CreateUserUseCase createUserUseCase;
    private final ObjectMapper objectMapper;

    /**
     * Consumes a user registration event from Kafka and creates a new user.
     *
     * @param payload the JSON payload of the user registration event
     */
    @KafkaListener(topics = "${app.kafka.topics.user-registered}", groupId = "${spring.kafka.consumer.group-id:user-service-group}")
    public void consume(String payload) {
        log.debug("Received UserRegisteredEvent payload");
        try {
            UserRegisteredEvent event = objectMapper.readValue(payload, UserRegisteredEvent.class);
            createUserUseCase.createUser(event);
            log.info("Successfully processed UserRegisteredEvent for user: {}", event.userId());
        } catch (JsonProcessingException e) {
            log.error("Failed to parse UserRegisteredEvent payload", e);
        } catch (Exception e) {
            log.error("Error processing UserRegisteredEvent", e);
        }
    }
}
