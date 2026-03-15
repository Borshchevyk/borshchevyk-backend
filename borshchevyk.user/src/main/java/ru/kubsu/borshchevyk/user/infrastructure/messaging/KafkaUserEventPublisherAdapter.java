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

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaUserEventPublisherAdapter implements UserEventPublisherPort {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.kafka.topics.user-updated:user-updated-events}")
    private String userUpdatedTopic;

    @Value("${app.kafka.topics.user-deleted:user-deleted-events}")
    private String userDeletedTopic;

    @Override
    public void publishUpdated(UserUpdatedEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(userUpdatedTopic, event.userId().toString(), payload);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize UserUpdatedEvent", e);
        }
    }

    @Override
    public void publishDeleted(UserDeletedEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(userDeletedTopic, event.userId().toString(), payload);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize UserDeletedEvent", e);
        }
    }
}
