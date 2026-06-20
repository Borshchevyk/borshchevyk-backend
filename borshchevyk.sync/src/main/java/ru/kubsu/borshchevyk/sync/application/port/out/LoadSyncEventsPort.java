package ru.kubsu.borshchevyk.sync.application.port.out;

import ru.kubsu.borshchevyk.sync.domain.model.SyncEvent;
import ru.kubsu.borshchevyk.sync.domain.model.VectorClock;

import java.util.List;

/**
 * Outbound port for loading synchronization events.
 *
 * @author Aleksey Timko
 */
public interface LoadSyncEventsPort {
    /**
     * Loads all events that are strictly AFTER the given client clock,
     * or concurrent with it.
     */
    List<SyncEvent> loadEventsAfterOrConcurrent(VectorClock clientClock, java.util.UUID userId, int limit);

    /**
     * Loads the current server vector clock (the merge of all events).
     */
    VectorClock loadCurrentServerClock();

    /**
     * Loads and locks the current server vector clock for atomic updates.
     */
    VectorClock loadAndLockCurrentServerClock();
}
