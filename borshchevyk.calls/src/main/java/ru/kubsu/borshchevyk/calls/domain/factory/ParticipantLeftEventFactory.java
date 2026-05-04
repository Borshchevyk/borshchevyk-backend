package ru.kubsu.borshchevyk.calls.domain.factory;

import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.calls.domain.event.call_room.CallRoomEvent;
import ru.kubsu.borshchevyk.calls.domain.event.call_room.CallRoomParticipantLeftEvent;

@Component
public class ParticipantLeftEventFactory implements CallRoomEventFactory {

    @Override
    public boolean supports(CallRoomEvent.Types type) {
        return type == CallRoomEvent.Types.PARTICIPANT_LEFT;
    }

    @Override
    public CallRoomEvent create(String roomId, String participantIdentity) {
        return new CallRoomParticipantLeftEvent(roomId, participantIdentity);
    }
}
