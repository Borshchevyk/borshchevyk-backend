package ru.kubsu.borshchevyk.calls.domain.factory;

import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.calls.domain.event.call_room.CallRoomEvent;
import ru.kubsu.borshchevyk.calls.domain.event.call_room.CallRoomFinishedEvent;

@Component
public class RoomFinishedEventFactory implements CallRoomEventFactory {

    @Override
    public boolean supports(CallRoomEvent.Types type) {
        return type == CallRoomEvent.Types.ROOM_FINISHED;
    }

    @Override
    public CallRoomEvent create(String roomId, String participantIdentity) {
        return new CallRoomFinishedEvent(roomId);
    }
}