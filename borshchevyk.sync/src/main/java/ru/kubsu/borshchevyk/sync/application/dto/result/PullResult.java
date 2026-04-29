package ru.kubsu.borshchevyk.sync.application.dto.result;

import lombok.Builder;
import ru.kubsu.borshchevyk.sync.domain.model.SyncEvent;

import java.util.List;

/**
 * Result of pulling events.
 *
 * @author Aleksey Timko
 */
@Builder
public record PullResult(
        List<SyncEvent> events,
        String nextToken,
        boolean hasMore
) {
}
