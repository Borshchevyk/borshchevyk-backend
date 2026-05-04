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
import ru.kubsu.borshchevyk.calls.application.dto.command.JoinCallCommand;
import ru.kubsu.borshchevyk.calls.application.port.in.JoinCallUseCase;
import ru.kubsu.borshchevyk.calls.domain.value.CallId;
import ru.kubsu.borshchevyk.calls.domain.value.UserId;
import ru.kubsu.borshchevyk.calls.infrastructure.adapter.in.web.dto.response.JoinCallResponse;
import ru.kubsu.borshchevyk.calls.infrastructure.adapter.in.web.mapper.CallMapper;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/calls")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Calls", description = "API for managing WebRTC calls via LiveKit")
public class JoinCallController {

    private final JoinCallUseCase joinCallUseCase;
    private final CallMapper callMapper;

    @Operation(
            summary = "Join a call",
            description = "Generates a LiveKit JWT token to join the specified call."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully generated join token",
                    content = @Content(schema = @Schema(implementation = JoinCallResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "User is not a participant of this call",
                    content = @Content
            ),
            @ApiResponse(responseCode = "409", description = "Call has ended", content = @Content),
            @ApiResponse(responseCode = "404", description = "Call not found", content = @Content)
    })
    @PostMapping("/{callId}/join")
    public ResponseEntity<JoinCallResponse> joinCall(
            @Parameter(description = "ID of the user making the request", required = true)
            @RequestHeader("X-User-Id") UUID currentUserId,
            @Parameter(description = "ID of the call to join", required = true)
            @PathVariable UUID callId
    ) {
        log.debug("User {} attempting to join call {}", currentUserId, callId);

        JoinCallCommand command = new JoinCallCommand(
                new CallId(callId),
                new UserId(currentUserId)
        );

        String token = joinCallUseCase.joinCall(command);
        return ResponseEntity.ok(new JoinCallResponse(token));
    }
}
