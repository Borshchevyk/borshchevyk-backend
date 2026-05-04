package ru.kubsu.borshchevyk.calls.application.port.out;

import ru.kubsu.borshchevyk.calls.domain.model.Call;
import ru.kubsu.borshchevyk.calls.domain.value.CallId;

import java.util.Optional;

/**
 * Port for loading Call entities from persistence.
 */
public interface LoadCallPort {
    Optional<Call> loadCall(CallId callId);
}
