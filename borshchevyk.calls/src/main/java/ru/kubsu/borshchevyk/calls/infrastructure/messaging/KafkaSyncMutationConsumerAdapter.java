package ru.kubsu.borshchevyk.calls.infrastructure.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.calls.application.dto.command.EndCallCommand;
import ru.kubsu.borshchevyk.calls.application.dto.command.InitiateCallCommand;
import ru.kubsu.borshchevyk.calls.application.dto.command.JoinCallCommand;
import ru.kubsu.borshchevyk.calls.application.dto.command.LeaveCallCommand;
import ru.kubsu.borshchevyk.calls.application.port.in.EndCallUseCase;
import ru.kubsu.borshchevyk.calls.application.port.in.InitiateCallUseCase;
import ru.kubsu.borshchevyk.calls.application.port.in.JoinCallUseCase;
import ru.kubsu.borshchevyk.calls.application.port.in.LeaveCallUseCase;
import ru.kubsu.borshchevyk.calls.domain.exception.CallNotFoundException;
import ru.kubsu.borshchevyk.calls.domain.value.CallId;
import ru.kubsu.borshchevyk.calls.domain.value.UserId;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Adapter for consuming offline mutations pushed from the sync-service.
 *
 * @author Aleksey Timko
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaSyncMutationConsumerAdapter {

    private final InitiateCallUseCase initiateCallUseCase;
    private final EndCallUseCase endCallUseCase;
    private final JoinCallUseCase joinCallUseCase;
    private final LeaveCallUseCase leaveCallUseCase;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${app.kafka.topics.sync-mutations:sync-mutations}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeSyncMutation(String payload) {
        log.info("Received sync mutation from Kafka");
        try {
            JsonNode root = objectMapper.readTree(payload);
            String eventType = root.get("eventType").asText();

            if (!eventType.equals("CALL_EVENT")) {
                return; // We only care about call domain events here
            }

            String operationPayload = root.get("payload").asText();
            JsonNode opNode = objectMapper.readTree(operationPayload);
            
            // Expected that client includes a "callAction" inside the payload to distinguish between events
            String action = opNode.has("callAction") ? opNode.get("callAction").asText() : "UNKNOWN";

            switch (action) {
                case "INITIATE" -> handleInitiateCall(opNode);
                case "END" -> handleEndCall(opNode);
                case "JOIN" -> handleJoinCall(opNode);
                case "LEAVE" -> handleLeaveCall(opNode);
                default -> log.debug("Ignored CALL_EVENT sync mutation with action {}", action);
            }
        } catch (JsonProcessingException e) {
            log.error("Failed to parse sync mutation payload", e);
        } catch (Exception e) {
            log.error("Error processing sync mutation", e);
        }
    }

    private void handleInitiateCall(JsonNode opNode) {
        try {
            UserId initiatorId = new UserId(UUID.fromString(opNode.get("initiatorId").asText()));
            Set<UserId> participantIds = new HashSet<>();
            if (opNode.has("participantIds") && opNode.get("participantIds").isArray()) {
                for (JsonNode idNode : opNode.get("participantIds")) {
                    participantIds.add(new UserId(UUID.fromString(idNode.asText())));
                }
            }

            InitiateCallCommand command = new InitiateCallCommand(initiatorId, participantIds, true);
            initiateCallUseCase.initiateCall(command);
        } catch (Exception e) {
            log.error("Failed to apply CALL INITIATE sync mutation", e);
        }
    }

    private void handleEndCall(JsonNode opNode) {
        try {
            CallId callId = new CallId(UUID.fromString(opNode.get("callId").asText()));
            UserId userId = new UserId(UUID.fromString(opNode.get("userId").asText()));

            EndCallCommand command = new EndCallCommand(callId, userId, true);
            endCallUseCase.endCall(command);
        } catch (CallNotFoundException e) {
            log.warn("Could not apply CALL END sync mutation: Call not found");
        } catch (Exception e) {
            log.error("Failed to apply CALL END sync mutation", e);
        }
    }

    private void handleJoinCall(JsonNode opNode) {
        try {
            CallId callId = new CallId(UUID.fromString(opNode.get("callId").asText()));
            UserId userId = new UserId(UUID.fromString(opNode.get("userId").asText()));

            JoinCallCommand command = new JoinCallCommand(callId, userId, true);
            joinCallUseCase.joinCall(command);
        } catch (CallNotFoundException e) {
            log.warn("Could not apply CALL JOIN sync mutation: Call not found");
        } catch (Exception e) {
            log.error("Failed to apply CALL JOIN sync mutation", e);
        }
    }

    private void handleLeaveCall(JsonNode opNode) {
        try {
            CallId callId = new CallId(UUID.fromString(opNode.get("callId").asText()));
            UserId userId = new UserId(UUID.fromString(opNode.get("userId").asText()));

            LeaveCallCommand command = new LeaveCallCommand(callId, userId, true);
            leaveCallUseCase.leaveCall(command);
        } catch (CallNotFoundException e) {
            log.warn("Could not apply CALL LEAVE sync mutation: Call not found");
        } catch (Exception e) {
            log.error("Failed to apply CALL LEAVE sync mutation", e);
        }
    }
}
