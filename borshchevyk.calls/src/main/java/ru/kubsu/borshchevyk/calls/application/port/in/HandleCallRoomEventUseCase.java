package ru.kubsu.borshchevyk.calls.application.port.in;

import ru.kubsu.borshchevyk.calls.domain.event.call_room.CallRoomEvent;

public interface HandleCallRoomEventUseCase {
    void handle(CallRoomEvent event);
}
