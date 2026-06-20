package ru.kubsu.borshchevyk.calls.domain.event.call_room;

public record CallRoomParticipantJoinedEvent(String roomId, String participantIdentity) implements CallRoomEvent {
}
