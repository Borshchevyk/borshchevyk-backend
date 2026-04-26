package ru.kubsu.borshchevyk.calls.infrastructure.adapter.out.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.calls.application.port.out.PublishCallEventPort;
import ru.kubsu.borshchevyk.calls.domain.model.Call;
import ru.kubsu.borshchevyk.calls.domain.model.UserId;
import ru.kubsu.borshchevyk.calls.infrastructure.adapter.out.messaging.dto.CallEventMessage;

import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Kafka implementation for publishing call events.
 *
 * @author Aleksey Timko
 * @since 2026-04-25
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CallEventKafkaAdapter implements PublishCallEventPort {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topics.call-events}")
    private String callEventsTopic;

    @Override
    public void publishCallInitiated(Call call) {
        log.info("Publishing CallInitiated event for call {}", call.getId().value());
        sendEvent(call, "INITIATED", call.getInitiatorId().value());
    }

    @Override
    public void publishCallEnded(Call call) {
        log.info("Publishing CallEnded event for call {}", call.getId().value());
        sendEvent(call, "ENDED", call.getInitiatorId().value());
    }

    @Override
    public void publishCallAccepted(Call call, UserId userId) {
        log.info("Publishing CallAccepted event for call {} by user {}", call.getId().value(), userId.value());
        sendEvent(call, "ACCEPTED", userId.value());
    }

    @Override
    public void publishCallRejected(Call call, UserId userId) {
        log.info("Publishing CallRejected event for call {} by user {}", call.getId().value(), userId.value());
        sendEvent(call, "REJECTED", userId.value());
    }

    private void sendEvent(Call call, String eventType, UUID actorId) {
        CallEventMessage message = CallEventMessage.builder()
                .callId(call.getId().value())
                .eventType(eventType)
                .actorId(actorId)
                .initiatorId(call.getInitiatorId().value())
                .timestamp(java.time.Instant.now())
                .participants(call.getParticipants().stream()
                        .map(UserId::value)
                        .collect(Collectors.toSet()))
                .build();

        kafkaTemplate.send(callEventsTopic, call.getId().value().toString(), message)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to send call event {} for call {}: {}", eventType, call.getId().value(), ex.getMessage());
                    } else {
                        log.debug("Successfully sent call event {} for call {}", eventType, call.getId().value());
                    }
                });
    }
}
