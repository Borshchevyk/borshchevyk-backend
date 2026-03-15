package ru.kubsu.borshchevyk.auth.infrastructure.adapter.out.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.auth.application.port.out.UserRegisteredEventPublisherPort;
import ru.kubsu.borshchevyk.auth.domain.event.UserRegisteredEvent;

@Component
@RequiredArgsConstructor
public class KafkaUserRegisteredEventPublisherAdapter implements UserRegisteredEventPublisherPort {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final JsonStringSerializer jsonStringSerializer;

    @Value("${app.kafka.topics.user-registered}")
    private String topic;

    @Override
    public void publish(UserRegisteredEvent event) {
        String payload = jsonStringSerializer.serialize(event);
        kafkaTemplate.send(topic, event.userId().toString(), payload);
    }
}
