package ru.kubsu.borshchevyk.calls.application.port.out;

import ru.kubsu.borshchevyk.calls.domain.model.Call;

import java.util.Optional;

public interface LoadCallByRoomIdPort {
    Optional<Call> loadCallByRoomId(String roomId);
}
