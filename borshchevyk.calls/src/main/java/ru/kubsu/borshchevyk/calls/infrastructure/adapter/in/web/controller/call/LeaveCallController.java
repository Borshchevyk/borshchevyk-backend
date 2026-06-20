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
import ru.kubsu.borshchevyk.calls.application.dto.command.LeaveCallCommand;
import ru.kubsu.borshchevyk.calls.application.port.in.LeaveCallUseCase;
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
public class LeaveCallController {

    private final LeaveCallUseCase leaveCallUseCase;
    private final WebCallMapper webCallMapper;

    @Operation(
            summary = "Leave a call",
            description = "Removes the user from the call's participant list."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully left the call",
                    content = @Content(schema = @Schema(implementation = CallResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Call not found", content = @Content)
    })
    @PostMapping("/{callId}/leave")
    public ResponseEntity<CallResponse> leaveCall(
            @Parameter(description = "ID of the user making the request", required = true)
            @RequestHeader("X-User-Id") UUID currentUserId,
            @Parameter(description = "ID of the call to leave", required = true)
            @PathVariable UUID callId
    ) {
        log.debug("User {} attempting to leave call {}", currentUserId, callId);

        LeaveCallCommand command = new LeaveCallCommand(
                new CallId(callId),
                new UserId(currentUserId)
        );

        Call call = leaveCallUseCase.leaveCall(command);
        return ResponseEntity.ok(webCallMapper.toResponse(call));
    }
}
