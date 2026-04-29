package ru.kubsu.borshchevyk.sync.infrastructure.adapter.in.web.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import ru.kubsu.borshchevyk.sync.domain.model.SyncEvent;

import java.util.List;

/**
 * Response for synchronization event pulling.
 *
 * @author Aleksey Timko
 */
@Builder
@Schema(description = "Response containing synchronization events and pagination details")
public record SyncResponse(
        @Schema(description = "List of synchronization events")
        List<SyncEvent> events,

        @Schema(description = "Token to use for fetching the next batch of events", example = "eyJzZXEiOjIwfQ==")
        String nextToken,

        @Schema(description = "Indicates if there are more events to fetch", example = "true")
        boolean hasMore
) {
}
