package ru.kubsu.borshchevyk.sync.infrastructure.adapter.in.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kubsu.borshchevyk.sync.application.dto.command.PullEventsCommand;
import ru.kubsu.borshchevyk.sync.application.dto.result.PullResult;
import ru.kubsu.borshchevyk.sync.application.port.in.PullEventsUseCase;
import ru.kubsu.borshchevyk.sync.infrastructure.adapter.in.web.dto.response.SyncResponse;
import ru.kubsu.borshchevyk.sync.infrastructure.adapter.in.web.mapper.SyncWebMapper;

import java.util.UUID;

/**
 * Controller for handling synchronization requests.
 *
 * @author Aleksey Timko
 */
@RestController
@RequestMapping("/api/v1/sync")
@RequiredArgsConstructor
@Tag(name = "Sync API", description = "Endpoints for synchronizing offline clients")
public class SyncController {

    private final PullEventsUseCase pullEventsUseCase;
    private final SyncWebMapper syncWebMapper;

    @GetMapping("/pull")
    @Operation(
            summary = "Pull synchronization events",
            description = "Fetches a batch of synchronization events for the requesting user based on the provided sync token.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Events fetched successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SyncResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid sync token or parameters", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
            }
    )
    public ResponseEntity<SyncResponse> pullEvents(
            @Parameter(description = "ID of the user requesting synchronization", required = true)
            @RequestHeader("X-User-Id") UUID requesterId,

            @Parameter(description = "Token representing the last synchronized sequence number. Leave empty for initial sync.")
            @RequestParam(required = false) String syncToken,

            @Parameter(description = "Maximum number of events to fetch (default: 100)")
            @RequestParam(defaultValue = "100") int limit) {
        
        PullEventsCommand command = PullEventsCommand.builder()
                .requesterId(requesterId)
                .syncToken(syncToken)
                .limit(limit)
                .build();

        PullResult result = pullEventsUseCase.pull(command);

        SyncResponse response = SyncResponse.builder()
                .events(syncWebMapper.toDtoList(result.events()))
                .nextToken(result.nextToken())
                .hasMore(result.hasMore())
                .build();

        return ResponseEntity.ok(response);
    }
}
