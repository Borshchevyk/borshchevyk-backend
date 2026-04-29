package ru.kubsu.borshchevyk.calls.infrastructure.adapter.in.web.controller;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kubsu.borshchevyk.calls.application.port.in.HandleLiveKitWebhookUseCase;
import ru.kubsu.borshchevyk.calls.infrastructure.adapter.in.web.dto.LiveKitWebhookEventDto;

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

    @PostMapping
    public ResponseEntity<Void> receiveWebhook(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody LiveKitWebhookEventDto payload) {
            
        try {
            String eventType = payload.event();
            String roomId = payload.room() != null ? payload.room().name() : null;
            String participantIdentity = payload.participant() != null ? payload.participant().identity() : null;

            log.info("Received LiveKit webhook: event={}, room={}", eventType, roomId);

            if (eventType == null) {
                return ResponseEntity.badRequest().build();
            }

            switch (eventType) {
                case "room_finished":
                    if (roomId != null) {
                        handleLiveKitWebhookUseCase.handleRoomFinished(roomId);
                    }
                    break;
                case "participant_joined":
                    if (roomId != null && participantIdentity != null) {
                        handleLiveKitWebhookUseCase.handleParticipantJoined(roomId, participantIdentity);
                    }
                    break;
                case "participant_left":
                    if (roomId != null && participantIdentity != null) {
                        handleLiveKitWebhookUseCase.handleParticipantLeft(roomId, participantIdentity);
                    }
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
