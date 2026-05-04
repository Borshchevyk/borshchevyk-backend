package ru.kubsu.borshchevyk.calls.application.port.out;

import ru.kubsu.borshchevyk.calls.domain.model.Call;

/**
 * Port for saving Call entities to persistence.
 */
public interface SaveCallPort {
    Call saveCall(Call call);
}
