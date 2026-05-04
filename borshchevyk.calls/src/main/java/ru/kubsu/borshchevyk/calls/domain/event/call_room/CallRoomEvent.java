package ru.kubsu.borshchevyk.calls.domain.event.call_room;

import lombok.Getter;

import java.util.Arrays;

public sealed interface CallRoomEvent permits CallRoomFinishedEvent, CallRoomParticipantJoinedEvent, CallRoomParticipantLeftEvent {
    String roomId();

    @Getter
    enum Types {
        ROOM_FINISHED("room_finished"),
        PARTICIPANT_JOINED("participant_joined"),
        PARTICIPANT_LEFT("participant_left");

        private final String name;

        public static Types fromString(String type) {
            return Arrays.stream(Types.values())
                    .filter((eventType) -> eventType.getName().equalsIgnoreCase(type))
                    .findFirst()
                    .orElseThrow();
        }

        Types(String name) {
            this.name = name;
        }
    }
}
