package ru.kubsu.borshchevyk.calls.infrastructure.adapter.in.web.controller.livekit;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kubsu.borshchevyk.calls.domain.factory.CallRoomEventFactoryResolver;
import ru.kubsu.borshchevyk.calls.application.port.in.HandleCallRoomEventUseCase;
import ru.kubsu.borshchevyk.calls.domain.event.call_room.CallRoomEvent;
import ru.kubsu.borshchevyk.calls.infrastructure.adapter.in.web.dto.request.LiveKitWebhookEventRequest;

/**
 * Controller for receiving webhooks from the LiveKit server.
 * Hidden from Swagger because it's an internal machine-to-machine API.
 */
@RestController
@RequestMapping("/api/v1/calls/webhook")
@RequiredArgsConstructor
@Slf4j
@Hidden
public class LiveKitWebhookController {

    private final HandleCallRoomEventUseCase handleCallRoomEventUseCase;
    private final CallRoomEventFactoryResolver callRoomEventFactoryResolver;

    @PostMapping
    public ResponseEntity<Void> receiveWebhook(@RequestBody LiveKitWebhookEventRequest payload) {
        try {
            CallRoomEvent.Types eventType = CallRoomEvent.Types.fromString(payload.event());
            String roomId = payload.room() != null ? payload.room().name() : null;
            String participantIdentity = payload.participant() != null ? payload.participant().identity() : null;

            log.info("Received LiveKit webhook: event={}, room={}", eventType, roomId);

            if (eventType == null) {
                return ResponseEntity.badRequest().build();
            }

            CallRoomEvent event = callRoomEventFactoryResolver.resolve(
                    eventType,
                    roomId,
                    participantIdentity
            );

            handleCallRoomEventUseCase.handle(event);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Failed to process LiveKit webhook", e);
            return ResponseEntity.badRequest().build();
        }
    }
}
