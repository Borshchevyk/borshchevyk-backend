package ru.kubsu.borshchevyk.sync.infrastructure.adapter.in.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kubsu.borshchevyk.sync.application.dto.command.PushSyncEventsCommand;
import ru.kubsu.borshchevyk.sync.application.port.in.PushSyncEventsUseCase;
import ru.kubsu.borshchevyk.sync.domain.model.VectorClock;
import ru.kubsu.borshchevyk.sync.infrastructure.adapter.in.web.dto.response.SyncEventDto;
import ru.kubsu.borshchevyk.sync.infrastructure.adapter.in.web.mapper.SyncWebMapper;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for pushing synchronization events.
 *
 * @author Aleksey Timko
 */
@RestController
@RequestMapping("/api/v1/sync")
@RequiredArgsConstructor
@Tag(name = "Sync API", description = "Endpoints for synchronizing offline clients")
public class PushSyncEventsController {

    private final PushSyncEventsUseCase pushSyncEventsUseCase;
    private final SyncWebMapper syncWebMapper;

    @PostMapping("/push")
    @Operation(
            summary = "Push synchronization events",
            description = "Pushes client's offline changes to the server to be merged.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Events pushed and merged successfully. Returns the new server Vector Clock.")
            }
    )
    public ResponseEntity<VectorClock> pushEvents(
            @RequestHeader("X-User-Id") java.util.UUID userId,
            @RequestBody List<SyncEventDto> eventsDto) {
        
        List<ru.kubsu.borshchevyk.sync.domain.model.SyncEvent> events = eventsDto.stream()
                .map(dto -> {
                    ru.kubsu.borshchevyk.sync.domain.model.SyncEvent event = syncWebMapper.toDomain(dto);
                    return new ru.kubsu.borshchevyk.sync.domain.model.SyncEvent(
                            event.id(), event.entityId(), userId, event.eventType(),
                            event.payload(), event.vectorClock(), event.timestamp()
                    );
                })
                .collect(Collectors.toList());

        PushSyncEventsCommand command = new PushSyncEventsCommand(events);
        VectorClock newServerClock = pushSyncEventsUseCase.push(command);
        return ResponseEntity.ok(newServerClock);
    }
}
