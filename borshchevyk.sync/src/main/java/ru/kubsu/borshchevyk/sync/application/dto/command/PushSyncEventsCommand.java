package ru.kubsu.borshchevyk.sync.application.dto.command;

import ru.kubsu.borshchevyk.sync.domain.model.SyncEvent;

import java.util.List;

/**
 * Command object for pushing client synchronization events.
 *
 * @author Aleksey Timko
 */
public record PushSyncEventsCommand(
        List<SyncEvent> clientEvents
) {
}
