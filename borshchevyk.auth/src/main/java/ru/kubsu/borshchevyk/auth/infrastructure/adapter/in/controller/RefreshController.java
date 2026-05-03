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
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kubsu.borshchevyk.auth.application.dto.command.RefreshCommand;
import ru.kubsu.borshchevyk.auth.application.port.in.RefreshUseCase;
import ru.kubsu.borshchevyk.auth.domain.model.result.VerifyResult;
import ru.kubsu.borshchevyk.auth.infrastructure.adapter.in.dto.request.RefreshRequest;
import ru.kubsu.borshchevyk.auth.infrastructure.adapter.in.dto.response.VerifyResponse;
import ru.kubsu.borshchevyk.auth.infrastructure.mapper.RefreshMapper;
import ru.kubsu.borshchevyk.auth.infrastructure.mapper.VerifyMapper;

/**
 * REST controller for refreshing tokens.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
@Slf4j
public class RefreshController {

    private final RefreshUseCase refreshUseCase;
    private final RefreshMapper refreshMapper;
    private final VerifyMapper verifyMapper;

    /**
     * Refreshes access and refresh tokens.
     *
     * @param refreshRequest the refresh details
     * @return the new JWT tokens if successful
     */
    @Operation(
            summary = "Refresh tokens",
            description = "Refreshes access and refresh tokens using a valid refresh token."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tokens successfully refreshed",
                    content = @Content(schema = @Schema(implementation = VerifyResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/refresh")
    public ResponseEntity<VerifyResponse> refresh(
            @Valid @RequestBody RefreshRequest refreshRequest
    ) {
        log.info("Refresh tokens requested");
        RefreshCommand refreshCommand = refreshMapper.toCommand(refreshRequest);
        VerifyResult verifyResult = refreshUseCase.refresh(refreshCommand);
        VerifyResponse verifyResponse = verifyMapper.toResponse(verifyResult);
        log.info("Tokens refreshed successfully");
        return ResponseEntity.ok(verifyResponse);
    }
}
