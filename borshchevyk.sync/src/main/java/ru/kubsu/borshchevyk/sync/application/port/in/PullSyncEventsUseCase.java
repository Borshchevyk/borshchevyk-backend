package ru.kubsu.borshchevyk.sync.application.port.in;

import ru.kubsu.borshchevyk.sync.application.dto.query.PullSyncEventsQuery;
import ru.kubsu.borshchevyk.sync.domain.model.SyncEvent;

import java.util.List;

/**
 * Use case for pulling synchronization events based on a client's vector clock.
 *
 * @author Aleksey Timko
 */
public interface PullSyncEventsUseCase {
    List<SyncEvent> pull(PullSyncEventsQuery query);
}
