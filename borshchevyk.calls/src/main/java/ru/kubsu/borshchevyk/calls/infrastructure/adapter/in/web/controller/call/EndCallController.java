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
import ru.kubsu.borshchevyk.calls.application.dto.command.EndCallCommand;
import ru.kubsu.borshchevyk.calls.application.port.in.EndCallUseCase;
import ru.kubsu.borshchevyk.calls.domain.model.Call;
import ru.kubsu.borshchevyk.calls.domain.value.CallId;
import ru.kubsu.borshchevyk.calls.domain.value.UserId;
import ru.kubsu.borshchevyk.calls.infrastructure.adapter.in.web.dto.response.CallResponse;
import ru.kubsu.borshchevyk.calls.infrastructure.adapter.in.web.mapper.CallMapper;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/calls")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Calls", description = "API for managing WebRTC calls via LiveKit")
public class EndCallController {

    private final EndCallUseCase endCallUseCase;
    private final CallMapper callMapper;

    @Operation(
            summary = "End a call",
            description = "Terminates an active call. Only the initiator can perform this."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Call ended successfully",
                    content = @Content(schema = @Schema(implementation = CallResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Only the initiator can end the call",
                    content = @Content
            ),
            @ApiResponse(responseCode = "404", description = "Call not found", content = @Content)
    })
    @PostMapping("/{callId}/end")
    public ResponseEntity<CallResponse> endCall(
            @Parameter(description = "ID of the user making the request", required = true)
            @RequestHeader("X-User-Id") UUID currentUserId,
            @Parameter(description = "ID of the call to end", required = true)
            @PathVariable UUID callId
    ) {
        log.debug("User {} attempting to end call {}", currentUserId, callId);

        EndCallCommand command = new EndCallCommand(
                new CallId(callId),
                new UserId(currentUserId)
        );

        Call call = endCallUseCase.endCall(command);
        return ResponseEntity.ok(callMapper.toResponse(call));
    }
}
