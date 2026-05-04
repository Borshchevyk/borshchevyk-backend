package ru.kubsu.borshchevyk.calls.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.kubsu.borshchevyk.calls.domain.handler.CallRoomEventHandler;
import ru.kubsu.borshchevyk.calls.application.port.in.HandleCallRoomEventUseCase;
import ru.kubsu.borshchevyk.calls.domain.event.call_room.CallRoomEvent;

import java.util.List;

/**
 * Service implementing LiveKit Webhook handling use cases.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LiveKitWebhookHandlingService implements HandleCallRoomEventUseCase {

    private final List<CallRoomEventHandler<CallRoomEvent>> handlers;

    @Override
    public void handle(CallRoomEvent event) {
        handlers.stream()
                .filter(f -> f.canHandle(event))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported event: " + event))
                .handle(event);
    }
}