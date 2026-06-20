package ru.kubsu.borshchevyk.calls.domain.factory;

import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.calls.domain.event.call_room.CallRoomEvent;
import ru.kubsu.borshchevyk.calls.domain.event.call_room.CallRoomParticipantJoinedEvent;

@Component
public class ParticipantJoinedEventFactory implements CallRoomEventFactory {

    @Override
    public boolean supports(CallRoomEvent.Types type) {
        return type == CallRoomEvent.Types.PARTICIPANT_JOINED;
    }

    @Override
    public CallRoomEvent create(String roomId, String participantIdentity) {
        return new CallRoomParticipantJoinedEvent(roomId, participantIdentity);
    }
}
