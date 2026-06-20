package ru.kubsu.borshchevyk.sync.application.port.out;

import ru.kubsu.borshchevyk.sync.domain.model.SyncEvent;

/**
 * Outbound port for broadcasting synchronization events to clients in real-time.
 *
 * @author Aleksey Timko
 */
public interface BroadcastSyncEventPort {
    void broadcast(SyncEvent event);
}
