package ru.kubsu.borshchevyk.calls.domain.event.call_room;

public record CallRoomParticipantLeftEvent(String roomId, String participantIdentity) implements CallRoomEvent {
}
