package ru.kubsu.borshchevyk.message.infrastructure.messaging;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response.ShortUserDto;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.facade.UserEnrichmentService;
import ru.kubsu.borshchevyk.message.infrastructure.redis.dto.NotificationDto;

import ru.kubsu.borshchevyk.message.domain.model.call.CallEventType;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaCallEventConsumerAdapter {

    private final ObjectMapper objectMapper;
    private final StringRedisTemplate redisTemplate;
    private final UserEnrichmentService userEnrichmentService;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CallEventMessage {
        private String callId;
        private String eventType;
        private String actorId;
        private String initiatorId;
        private String timestamp;
        private Set<String> participants;
    }

    @KafkaListener(topics = "${app.kafka.topics.call-events}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeCallEvent(String payload) {
        log.debug("Received call event payload: {}", payload);
        try {
            CallEventMessage callEventMessage = objectMapper.readValue(payload, CallEventMessage.class);

            ShortUserDto initiator = null;
            if (callEventMessage.getInitiatorId() != null) {
                try {
                    initiator = userEnrichmentService.enrichUser(UUID.fromString(callEventMessage.getInitiatorId()));
                } catch (Exception e) {
                    log.warn("Failed to enrich user for initiatorId {}: {}", callEventMessage.getInitiatorId(), e.getMessage());
                }
            }

            ShortUserDto actor = null;
            if (callEventMessage.getActorId() != null) {
                try {
                    actor = userEnrichmentService.enrichUser(UUID.fromString(callEventMessage.getActorId()));
                } catch (Exception e) {
                    log.warn("Failed to enrich user for actorId {}: {}", callEventMessage.getActorId(), e.getMessage());
                }
            }

            NotificationDto.CallEventDto callEventDto = new NotificationDto.CallEventDto(
                    callEventMessage.getCallId(),
                    callEventMessage.getEventType(),
                    initiator,
                    actor,
                    callEventMessage.getTimestamp()
            );

            if (callEventMessage.getParticipants() != null) {
                CallEventType eventType = CallEventType.fromString(callEventMessage.getEventType());
                Set<String> targetParticipants = eventType.determineTargetParticipants(
                        callEventMessage.getParticipants(),
                        callEventMessage.getActorId(),
                        callEventMessage.getInitiatorId()
                );

                for (String participantId : targetParticipants) {
                    NotificationDto notification = new NotificationDto(
                            participantId,
                            null,
                            null,
                            callEventDto
                    );

                    String json = objectMapper.writeValueAsString(notification);
                    redisTemplate.convertAndSend("ws.messages", json);
                    log.debug("Published call event to Redis for user: {}", participantId);
                }
            }
        } catch (Exception e) {
            log.error("Failed to process CallEventMessage: {}", e.getMessage(), e);
        }
    }
}