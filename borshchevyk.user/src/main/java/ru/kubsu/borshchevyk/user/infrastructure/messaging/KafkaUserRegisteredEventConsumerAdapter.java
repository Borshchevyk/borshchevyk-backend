package ru.kubsu.borshchevyk.user.infrastructure.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.user.application.port.in.CreateUserUseCase;
import ru.kubsu.borshchevyk.user.domain.event.UserRegisteredEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaUserRegisteredEventConsumerAdapter {

    private final CreateUserUseCase createUserUseCase;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${app.kafka.topics.user-registered}", groupId = "${spring.kafka.consumer.group-id:user-service-group}")
    public void consume(String payload) {
        try {
            UserRegisteredEvent event = objectMapper.readValue(payload, UserRegisteredEvent.class);
            createUserUseCase.createUser(event);
            log.info("Successfully processed UserRegisteredEvent for user: {}", event.userId());
        } catch (JsonProcessingException e) {
            log.error("Failed to parse UserRegisteredEvent payload: {}", payload, e);
        } catch (Exception e) {
            log.error("Error processing UserRegisteredEvent", e);
        }
    }
}
