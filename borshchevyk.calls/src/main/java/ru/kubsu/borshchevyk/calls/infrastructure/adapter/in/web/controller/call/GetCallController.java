package ru.kubsu.borshchevyk.calls.infrastructure.adapter.in.web.controller.call;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kubsu.borshchevyk.calls.application.dto.query.GetCallQuery;
import ru.kubsu.borshchevyk.calls.application.port.in.GetCallUseCase;
import ru.kubsu.borshchevyk.calls.domain.model.Call;
import ru.kubsu.borshchevyk.calls.domain.value.CallId;
import ru.kubsu.borshchevyk.calls.domain.value.UserId;
import ru.kubsu.borshchevyk.calls.infrastructure.adapter.in.web.dto.response.CallResponse;
import ru.kubsu.borshchevyk.calls.infrastructure.adapter.in.web.mapper.WebCallMapper;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/calls")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Calls", description = "API for managing WebRTC calls via LiveKit")
public class GetCallController {

    private final GetCallUseCase getCallUseCase;
    private final WebCallMapper webCallMapper;

    @Operation(
            summary = "Get call details",
            description = "Retrieves information about a specific call."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Call retrieved successfully",
                    content = @Content(schema = @Schema(implementation = CallResponse.class))
            ),
            @ApiResponse(responseCode = "403", description = "User is not a participant of this call", content = @Content),
            @ApiResponse(responseCode = "404", description = "Call not found", content = @Content)
    })
    @GetMapping("/{callId}")
    public ResponseEntity<CallResponse> getCall(
            @Parameter(description = "ID of the user making the request", required = true)
            @RequestHeader("X-User-Id") UUID currentUserId,
            @Parameter(description = "ID of the call to retrieve", required = true)
            @PathVariable UUID callId
    ) {
        log.debug("User {} fetching call {}", currentUserId, callId);

        GetCallQuery query = new GetCallQuery(
                new CallId(callId),
                new UserId(currentUserId)
        );

        Call call = getCallUseCase.getCall(query);
        return ResponseEntity.ok(webCallMapper.toResponse(call));
    }
}