package ru.kubsu.borshchevyk.auth.infrastructure.adapter.out.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.auth.application.port.out.UserRegisteredEventPublisherPort;
import ru.kubsu.borshchevyk.auth.domain.event.UserRegisteredEvent;

/**
 * Adapter for publishing user registration events to Kafka.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaUserRegisteredEventPublisherAdapter implements UserRegisteredEventPublisherPort {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final JsonStringSerializer jsonStringSerializer;

    @Value("${app.kafka.topics.user-registered}")
    private String topic;

    /**
     * Publishes a user registration event.
     *
     * @param event the event to publish
     */
    @Override
    public void publish(UserRegisteredEvent event) {
        log.info("Publishing UserRegisteredEvent for user: {}", event.userId());
        String payload = jsonStringSerializer.serialize(event);
        kafkaTemplate.send(topic, event.userId().toString(), payload);
        log.info("UserRegisteredEvent published to topic: {}", topic);
    }
}
