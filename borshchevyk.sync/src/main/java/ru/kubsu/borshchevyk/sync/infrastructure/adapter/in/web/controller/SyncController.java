package ru.kubsu.borshchevyk.sync.infrastructure.adapter.in.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kubsu.borshchevyk.sync.application.dto.command.PullEventsCommand;
import ru.kubsu.borshchevyk.sync.application.dto.result.PullResult;
import ru.kubsu.borshchevyk.sync.application.port.in.PullEventsUseCase;
import ru.kubsu.borshchevyk.sync.infrastructure.adapter.in.web.dto.response.SyncResponse;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sync")
@RequiredArgsConstructor
public class SyncController {

    private final PullEventsUseCase pullEventsUseCase;

    @GetMapping("/pull")
    public ResponseEntity<SyncResponse> pullEvents(
            @RequestHeader("X-User-Id") UUID requesterId,
            @RequestParam(required = false) String syncToken,
            @RequestParam(defaultValue = "100") int limit) {
        
        PullEventsCommand command = PullEventsCommand.builder()
                .requesterId(requesterId)
                .syncToken(syncToken)
                .limit(limit)
                .build();

        PullResult result = pullEventsUseCase.pull(command);

        SyncResponse response = SyncResponse.builder()
                .events(result.getEvents())
                .nextToken(result.getNextToken())
                .hasMore(result.isHasMore())
                .build();

        return ResponseEntity.ok(response);
    }
}