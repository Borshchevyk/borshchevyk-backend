package ru.kubsu.borshchevyk.calls.domain.factory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.calls.domain.event.call_room.CallRoomEvent;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CallRoomEventFactoryResolver {

    private final List<CallRoomEventFactory> factories;

    public CallRoomEvent resolve(CallRoomEvent.Types type, String roomId, String participantIdentity) {
        return factories.stream()
                .filter(f -> f.supports(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported event: " + type))
                .create(roomId, participantIdentity);
    }
}
