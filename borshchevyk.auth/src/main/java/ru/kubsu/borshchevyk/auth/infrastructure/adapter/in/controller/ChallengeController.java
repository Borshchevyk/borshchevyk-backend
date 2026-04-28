package ru.kubsu.borshchevyk.auth.infrastructure.adapter.in.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kubsu.borshchevyk.auth.application.dto.command.ChallengeCommand;
import ru.kubsu.borshchevyk.auth.application.port.in.ChallengeUseCase;
import org.springframework.http.ProblemDetail;
import ru.kubsu.borshchevyk.auth.domain.model.result.ChallengeResult;
import ru.kubsu.borshchevyk.auth.infrastructure.adapter.in.dto.request.ChallengeRequest;
import ru.kubsu.borshchevyk.auth.infrastructure.adapter.in.dto.response.ChallengeResponse;
import ru.kubsu.borshchevyk.auth.infrastructure.mapper.ChallengeMapper;

/**
 * REST controller for generating cryptographic challenges.
 *
 * @author Aleksey Timko
 * @since 2026-03-14
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
@Slf4j
public class ChallengeController {

    private final ChallengeUseCase challengeUseCase;
    private final ChallengeMapper challengeMapper;

    /**
     * Requests a cryptographic challenge for authentication.
     *
     * @param challengeRequest the challenge request details
     * @return the generated challenge
     */
    @Operation(summary = "Request cryptographic challenge", description = "Generates a cryptographic challenge for client verification.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Challenge successfully generated",
                    content = @Content(schema = @Schema(implementation = ChallengeResponse.class))),
            @ApiResponse(responseCode = "404", description = "Account not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/challenge")
    public ResponseEntity<ChallengeResponse> challenge(
            @Valid @RequestBody ChallengeRequest challengeRequest) {
        log.info("Challenge requested for user: {}", challengeRequest.userId());
        ChallengeCommand challengeCommand = challengeMapper.toCommand(challengeRequest);
        ChallengeResult challengeResult = challengeUseCase.challenge(challengeCommand);
        ChallengeResponse challengeResponse = challengeMapper.toResponse(challengeResult);
        log.info("Challenge generated for user: {}", challengeRequest.userId());
        return ResponseEntity.ok(challengeResponse);
    }
}
