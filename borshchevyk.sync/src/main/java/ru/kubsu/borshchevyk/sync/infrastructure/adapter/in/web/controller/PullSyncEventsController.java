package ru.kubsu.borshchevyk.sync.infrastructure.adapter.in.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kubsu.borshchevyk.sync.application.dto.query.PullSyncEventsQuery;
import ru.kubsu.borshchevyk.sync.application.port.in.PullSyncEventsUseCase;
import ru.kubsu.borshchevyk.sync.domain.model.SyncEvent;
import ru.kubsu.borshchevyk.sync.domain.model.VectorClock;
import ru.kubsu.borshchevyk.sync.infrastructure.adapter.in.web.dto.response.SyncEventDto;
import ru.kubsu.borshchevyk.sync.infrastructure.adapter.in.web.mapper.SyncWebMapper;

import java.util.List;

/**
 * Controller for pulling synchronization events.
 *
 * @author Aleksey Timko
 */
@RestController
@RequestMapping("/api/v1/sync")
@RequiredArgsConstructor
@Tag(name = "Sync API", description = "Endpoints for synchronizing offline clients")
public class PullSyncEventsController {

    private final PullSyncEventsUseCase pullSyncEventsUseCase;
    private final SyncWebMapper syncWebMapper;

    @PostMapping("/pull")
    @Operation(
            summary = "Pull synchronization events",
            description = "Fetches synchronization events that happened after the provided client vector clock.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Events fetched successfully")
            }
    )
    public ResponseEntity<ru.kubsu.borshchevyk.sync.infrastructure.adapter.in.web.dto.response.PullSyncEventsResponse> pullEvents(
            @RequestHeader("X-User-Id") java.util.UUID userId,
            @RequestParam(defaultValue = "100") int limit,
            @RequestBody VectorClock clientClock) {
        PullSyncEventsQuery query = new PullSyncEventsQuery(clientClock, userId, limit);
        
        List<SyncEvent> events = pullSyncEventsUseCase.pull(query);
        boolean hasMore = events.size() == limit;
        
        return ResponseEntity.ok(new ru.kubsu.borshchevyk.sync.infrastructure.adapter.in.web.dto.response.PullSyncEventsResponse(
                syncWebMapper.toDtoList(events),
                hasMore
        ));
    }
}
