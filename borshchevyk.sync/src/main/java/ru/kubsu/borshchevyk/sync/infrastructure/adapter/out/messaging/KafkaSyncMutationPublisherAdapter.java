package ru.kubsu.borshchevyk.sync.infrastructure.adapter.out.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.sync.application.port.out.PublishSyncMutationPort;
import ru.kubsu.borshchevyk.sync.domain.model.SyncEvent;

/**
 * Adapter for publishing client mutations to a unified Kafka topic.
 * Domain services will listen to this topic to apply offline changes.
 *
 * @author Aleksey Timko
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaSyncMutationPublisherAdapter implements PublishSyncMutationPort {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.kafka.topics.sync-mutations:sync-mutations}")
    private String syncMutationsTopic;

    @Override
    public void publish(SyncEvent event) {
        log.debug("Publishing sync mutation to topic {}: {}", syncMutationsTopic, event.id());
        try {
            // We publish the entire SyncEvent so domain services know the EventType
            // and have access to the payload and vector clock if needed.
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(syncMutationsTopic, event.entityId().toString(), message);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize SyncEvent for publishing mutation", e);
        }
    }
}
