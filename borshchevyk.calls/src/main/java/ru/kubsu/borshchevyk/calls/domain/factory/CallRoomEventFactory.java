package ru.kubsu.borshchevyk.calls.domain.factory;

import ru.kubsu.borshchevyk.calls.domain.event.call_room.CallRoomEvent;

public interface CallRoomEventFactory {
    boolean supports(CallRoomEvent.Types type);
    CallRoomEvent create(String roomId, String participantIdentity);
}
