package ru.kubsu.borshchevyk.calls.infrastructure.adapter.in.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kubsu.borshchevyk.calls.application.dto.command.EndCallCommand;
import ru.kubsu.borshchevyk.calls.application.dto.command.InitiateCallCommand;
import ru.kubsu.borshchevyk.calls.application.dto.command.JoinCallCommand;
import ru.kubsu.borshchevyk.calls.application.dto.query.GetCallQuery;
import ru.kubsu.borshchevyk.calls.application.port.in.ManageCallUseCase;
import ru.kubsu.borshchevyk.calls.domain.model.Call;
import ru.kubsu.borshchevyk.calls.domain.model.CallId;
import ru.kubsu.borshchevyk.calls.domain.model.UserId;
import ru.kubsu.borshchevyk.calls.infrastructure.adapter.in.web.dto.CallResponse;
import ru.kubsu.borshchevyk.calls.infrastructure.adapter.in.web.dto.InitiateCallRequest;
import ru.kubsu.borshchevyk.calls.infrastructure.adapter.in.web.dto.JoinCallResponse;
import ru.kubsu.borshchevyk.calls.infrastructure.adapter.in.web.mapper.CallWebMapper;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST Controller for managing Calls.
 *
 * @author Aleksey Timko
 * @since 2026-04-25
 */
@RestController
@RequestMapping("/api/v1/calls")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Calls", description = "API for managing WebRTC calls via LiveKit")
public class CallController {

    private final ManageCallUseCase manageCallUseCase;
    private final CallWebMapper callWebMapper;

    @Operation(summary = "Initiate a new call", description = "Creates a new call room and returns the call details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Call initiated successfully",
                    content = @Content(schema = @Schema(implementation = CallResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    @PostMapping
    public ResponseEntity<CallResponse> initiateCall(
            @Parameter(description = "ID of the user making the request", required = true)
            @RequestHeader("X-User-Id") UUID currentUserId,
            @Valid @RequestBody InitiateCallRequest request) {

        log.debug("User {} initiating call with participants: {}", currentUserId, request.participantIds());

        Set<UserId> participantIds = request.participantIds().stream()
                .map(UserId::new)
                .collect(Collectors.toSet());

        InitiateCallCommand command = new InitiateCallCommand(
                new UserId(currentUserId),
                participantIds
        );

        Call call = manageCallUseCase.initiateCall(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(callWebMapper.toResponse(call));
    }

    @Operation(summary = "Join a call", description = "Generates a LiveKit JWT token to join the specified call.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully generated join token",
                    content = @Content(schema = @Schema(implementation = JoinCallResponse.class))),
            @ApiResponse(responseCode = "400", description = "User is not a participant of this call or call has ended", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Call not found", content = @Content)
    })
    @PostMapping("/{callId}/join")
    public ResponseEntity<JoinCallResponse> joinCall(
            @Parameter(description = "ID of the user making the request", required = true)
            @RequestHeader("X-User-Id") UUID currentUserId,
            @Parameter(description = "ID of the call to join", required = true)
            @PathVariable UUID callId) {

        log.debug("User {} attempting to join call {}", currentUserId, callId);

        JoinCallCommand command = new JoinCallCommand(
                new CallId(callId),
                new UserId(currentUserId)
        );

        String token = manageCallUseCase.joinCall(command);
        return ResponseEntity.ok(new JoinCallResponse(token));
    }

    @Operation(summary = "Get call details", description = "Retrieves information about a specific call.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Call retrieved successfully",
                    content = @Content(schema = @Schema(implementation = CallResponse.class))),
            @ApiResponse(responseCode = "400", description = "User is not a participant of this call", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Call not found", content = @Content)
    })
    @GetMapping("/{callId}")
    public ResponseEntity<CallResponse> getCall(
            @Parameter(description = "ID of the user making the request", required = true)
            @RequestHeader("X-User-Id") UUID currentUserId,
            @Parameter(description = "ID of the call to retrieve", required = true)
            @PathVariable UUID callId) {

        log.debug("User {} fetching call {}", currentUserId, callId);

        GetCallQuery query = new GetCallQuery(
                new CallId(callId),
                new UserId(currentUserId)
        );

        Call call = manageCallUseCase.getCall(query);
        return ResponseEntity.ok(callWebMapper.toResponse(call));
    }

    @Operation(summary = "End a call", description = "Terminates an active call.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Call ended successfully",
                    content = @Content(schema = @Schema(implementation = CallResponse.class))),
            @ApiResponse(responseCode = "400", description = "User is not a participant of this call", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Call not found", content = @Content)
    })
    @PostMapping("/{callId}/end")
    public ResponseEntity<CallResponse> endCall(
            @Parameter(description = "ID of the user making the request", required = true)
            @RequestHeader("X-User-Id") UUID currentUserId,
            @Parameter(description = "ID of the call to end", required = true)
            @PathVariable UUID callId) {

        log.debug("User {} attempting to end call {}", currentUserId, callId);

        EndCallCommand command = new EndCallCommand(
                new CallId(callId),
                new UserId(currentUserId)
        );

        Call call = manageCallUseCase.endCall(command);
        return ResponseEntity.ok(callWebMapper.toResponse(call));
    }
}
