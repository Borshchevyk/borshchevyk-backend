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
import ru.kubsu.borshchevyk.auth.application.dto.command.VerifyCommand;
import ru.kubsu.borshchevyk.auth.application.port.in.VerifyUseCase;
import org.springframework.http.ProblemDetail;
import ru.kubsu.borshchevyk.auth.domain.model.result.VerifyResult;
import ru.kubsu.borshchevyk.auth.infrastructure.adapter.in.dto.request.VerifyRequest;
import ru.kubsu.borshchevyk.auth.infrastructure.adapter.in.dto.response.VerifyResponse;
import ru.kubsu.borshchevyk.auth.infrastructure.mapper.VerifyMapper;

/**
 * REST controller for verifying cryptographic challenges.
 *
 * @author Aleksey Timko
 * @since 2026-03-14
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
@Slf4j
public class VerifyController {

    private final VerifyUseCase verifyUseCase;
    private final VerifyMapper verifyMapper;

    /**
     * Verifies the cryptographic challenge signature.
     *
     * @param verifyRequest the verification details
     * @return the JWT tokens if verification is successful
     */
    @Operation(summary = "Verify cryptographic challenge", description = "Verifies the signature of the generated challenge to authenticate.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Challenge successfully verified",
                    content = @Content(schema = @Schema(implementation = VerifyResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid signature or challenge expired",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Account not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/verify")
    public ResponseEntity<VerifyResponse> verify(
            @Valid @RequestBody VerifyRequest verifyRequest) {
        log.info("Verifying challenge for user: {}", verifyRequest.userId());
        VerifyCommand verifyCommand = verifyMapper.toCommand(verifyRequest);
        VerifyResult verifyResult = verifyUseCase.verify(verifyCommand);
        VerifyResponse verifyResponse = verifyMapper.toResponse(verifyResult);
        log.info("Challenge verified successfully for user: {}", verifyRequest.userId());
        return ResponseEntity.ok(verifyResponse);
    }
}
