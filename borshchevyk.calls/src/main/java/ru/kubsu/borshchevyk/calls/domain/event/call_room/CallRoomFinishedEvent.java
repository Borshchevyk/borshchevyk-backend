package ru.kubsu.borshchevyk.calls.domain.event.call_room;

public record CallRoomFinishedEvent(String roomId) implements CallRoomEvent {
}
