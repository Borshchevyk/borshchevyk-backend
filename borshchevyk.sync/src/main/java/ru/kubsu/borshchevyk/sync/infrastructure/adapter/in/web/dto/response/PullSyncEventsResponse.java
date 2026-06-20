package ru.kubsu.borshchevyk.sync.infrastructure.adapter.in.web.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Response containing pulled sync events and pagination status")
public record PullSyncEventsResponse(
        @Schema(description = "List of sync events")
        List<SyncEventDto> events,

        @Schema(description = "True if there are more events to fetch", example = "false")
        boolean hasMore
) {
}
