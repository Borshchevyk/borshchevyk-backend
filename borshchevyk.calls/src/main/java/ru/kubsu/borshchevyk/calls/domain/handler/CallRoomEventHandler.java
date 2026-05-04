package ru.kubsu.borshchevyk.calls.domain.handler;

import ru.kubsu.borshchevyk.calls.domain.event.call_room.CallRoomEvent;

public interface CallRoomEventHandler<T extends CallRoomEvent> {
    boolean canHandle(CallRoomEvent event);
    void handle(T event);
}
