package ru.kubsu.borshchevyk.calls.infrastructure.adapter.in.web.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kubsu.borshchevyk.calls.application.port.in.HandleLiveKitWebhookUseCase;

/**
 * Controller for receiving webhooks from the LiveKit server.
 * Hidden from Swagger because it's an internal machine-to-machine API.
 *
 * @author Aleksey Timko
 * @since 2026-04-25
 */
@RestController
@RequestMapping("/api/v1/calls/webhook")
@RequiredArgsConstructor
@Slf4j
@Hidden
public class LiveKitWebhookController {

    private final HandleLiveKitWebhookUseCase handleLiveKitWebhookUseCase;
    private final ObjectMapper objectMapper;

    @PostMapping
    public ResponseEntity<Void> receiveWebhook(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody String payload) {
            
        try {
            JsonNode root = objectMapper.readTree(payload);
            String eventType = root.path("event").asText();
            JsonNode roomNode = root.path("room");
            String roomId = roomNode.path("name").asText();
            String participantIdentity = root.path("participant").path("identity").asText();

            log.info("Received LiveKit webhook: event={}, room={}", eventType, roomId);

            switch (eventType) {
                case "room_finished":
                    handleLiveKitWebhookUseCase.handleRoomFinished(roomId);
                    break;
                case "participant_joined":
                    handleLiveKitWebhookUseCase.handleParticipantJoined(roomId, participantIdentity);
                    break;
                case "participant_left":
                    handleLiveKitWebhookUseCase.handleParticipantLeft(roomId, participantIdentity);
                    break;
                default:
                    log.debug("Ignored webhook event: {}", eventType);
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Failed to process LiveKit webhook", e);
            return ResponseEntity.badRequest().build();
        }
    }
}
