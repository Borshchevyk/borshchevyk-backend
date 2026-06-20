package ru.kubsu.borshchevyk.sync.application.dto.query;

import ru.kubsu.borshchevyk.sync.domain.model.VectorClock;

/**
 * Query object for pulling synchronization events.
 *
 * @author Aleksey Timko
 */
public record PullSyncEventsQuery(
        VectorClock clientClock,
        java.util.UUID userId,
        int limit
) {
}
