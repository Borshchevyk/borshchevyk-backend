package ru.kubsu.borshchevyk.sync.application.dto.result;

import lombok.Builder;
import ru.kubsu.borshchevyk.sync.domain.model.SyncEvent;

import java.util.List;

/**
 * Result of pulling events.
 *
 * @author Aleksey Timko
 * @since 2026-03-01
 */
@Builder
public record PullResult(
        List<SyncEvent> events,
        String nextToken,
        boolean hasMore
) {
}
