package ru.kubsu.borshchevyk.sync.application.port.out;

import ru.kubsu.borshchevyk.sync.domain.model.SyncEvent;
import ru.kubsu.borshchevyk.sync.domain.model.VectorClock;

/**
 * Outbound port for saving a synchronization event to the journal.
 *
 * @author Aleksey Timko
 */
public interface SaveSyncEventPort {
    void save(SyncEvent event);
    void saveCurrentServerClock(VectorClock clock);
}
