package ru.kubsu.borshchevyk.sync.application.port.out;

import ru.kubsu.borshchevyk.sync.domain.model.SyncEvent;

import java.util.List;
import java.util.UUID;

/**
 * Output port for managing sync events in the persistence layer.
 *
 * @author Aleksey Timko
 */
public interface SyncEventPort {
    void save(SyncEvent event);
    List<SyncEvent> loadAfterSequence(UUID targetUserId, Long sequenceNumber, int limit);
    Long getLatestSequence(UUID targetUserId);
}
