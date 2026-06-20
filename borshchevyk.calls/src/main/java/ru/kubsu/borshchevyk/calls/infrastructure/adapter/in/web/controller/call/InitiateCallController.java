package ru.kubsu.borshchevyk.calls.infrastructure.adapter.in.web.controller.call;

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
import ru.kubsu.borshchevyk.calls.application.dto.command.InitiateCallCommand;
import ru.kubsu.borshchevyk.calls.application.port.in.InitiateCallUseCase;
import ru.kubsu.borshchevyk.calls.domain.model.Call;
import ru.kubsu.borshchevyk.calls.domain.value.UserId;
import ru.kubsu.borshchevyk.calls.infrastructure.adapter.in.web.dto.request.InitiateCallRequest;
import ru.kubsu.borshchevyk.calls.infrastructure.adapter.in.web.dto.response.CallResponse;
import ru.kubsu.borshchevyk.calls.infrastructure.adapter.in.web.mapper.WebCallMapper;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/calls")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Calls", description = "API for managing WebRTC calls via LiveKit")
public class InitiateCallController {

    private final InitiateCallUseCase initiateCallUseCase;
    private final WebCallMapper webCallMapper;

    @Operation(
            summary = "Initiate a new call",
            description = "Creates a new call room and returns the call details."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Call initiated successfully",
                    content = @Content(schema = @Schema(implementation = CallResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    @PostMapping
    public ResponseEntity<CallResponse> initiateCall(
            @Parameter(description = "ID of the user making the request", required = true)
            @RequestHeader("X-User-Id") UUID currentUserId,
            @Valid @RequestBody InitiateCallRequest request
    ) {
        log.debug("User {} initiating call with participants: {}", currentUserId, request.participantIds());

        Set<UserId> participantIds = request.participantIds().stream()
                .map(UserId::new)
                .collect(Collectors.toSet());

        InitiateCallCommand command = new InitiateCallCommand(
                new UserId(currentUserId),
                participantIds
        );

        Call call = initiateCallUseCase.initiateCall(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(webCallMapper.toResponse(call));
    }
}
