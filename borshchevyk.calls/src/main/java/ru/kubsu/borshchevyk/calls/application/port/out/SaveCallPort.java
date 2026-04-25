package ru.kubsu.borshchevyk.calls.application.port.out;

import ru.kubsu.borshchevyk.calls.domain.model.Call;

/**
 * Port for saving Call entities to persistence.
 *
 * @author Aleksey Timko
 * @since 2026-04-25
 */
public interface SaveCallPort {
    Call saveCall(Call call);
}
