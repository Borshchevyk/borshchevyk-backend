package ru.kubsu.borshchevyk.calls.infrastructure.adapter.out.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.calls.application.port.out.PublishCallEventPort;
import ru.kubsu.borshchevyk.calls.domain.event.call.CallEvent;
import ru.kubsu.borshchevyk.calls.infrastructure.adapter.out.messaging.dto.CallEventMessage;

import java.time.Instant;

/**
 * Kafka implementation for publishing call events.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CallEventKafkaAdapter implements PublishCallEventPort {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topics.call-events}")
    private String callEventsTopic;

    @Override
    public void publish(CallEvent event) {
        log.info("Publishing {} event for call {} and actorId {}", event.type().getName(), event.callId(), event.actorId());

        CallEventMessage message = CallEventMessage.builder()
                .callId(event.callId())
                .eventType(event.type().getName())
                .actorId(event.actorId())
                .initiatorId(event.initiatorId())
                .timestamp(Instant.now())
                .participants(event.participants())
                .build();

        kafkaTemplate.send(callEventsTopic, event.callId().toString(), message)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to send call event {} for call {}: {}",
                                event.type().getName(), event.callId(), ex.getMessage());
                    } else {
                        log.debug("Successfully sent call event {} for call {}",
                                event.type().getName(), event.callId());
                    }
                });
    }
}
