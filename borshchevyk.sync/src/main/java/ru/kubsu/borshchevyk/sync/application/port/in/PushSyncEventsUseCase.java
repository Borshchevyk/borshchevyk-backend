package ru.kubsu.borshchevyk.sync.application.port.in;

import ru.kubsu.borshchevyk.sync.application.dto.command.PushSyncEventsCommand;
import ru.kubsu.borshchevyk.sync.domain.model.VectorClock;

/**
 * Use case for pushing offline synchronization events from a client.
 *
 * @author Aleksey Timko
 */
public interface PushSyncEventsUseCase {
    VectorClock push(PushSyncEventsCommand command);
}
