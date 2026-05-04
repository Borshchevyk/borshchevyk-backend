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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kubsu.borshchevyk.auth.application.dto.command.ChangePasswordCommand;
import ru.kubsu.borshchevyk.auth.application.port.in.ChangePasswordUseCase;
import ru.kubsu.borshchevyk.auth.infrastructure.adapter.in.dto.request.ChangePasswordRequest;
import ru.kubsu.borshchevyk.auth.infrastructure.adapter.in.mapper.ChangePasswordMapper;

/**
 * REST controller for changing passwords.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
@Slf4j
public class ChangePasswordController {

    private final ChangePasswordUseCase changePasswordUseCase;
    private final ChangePasswordMapper changePasswordMapper;

    /**
     * Changes the user's password.
     *
     * @param changePasswordRequest the password change details
     * @return 200 OK if successful
     */
    @Operation(
            summary = "Change user password",
            description = "Updates the user's password if the old password matches."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password successfully changed",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "401", description = "Invalid old password",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Account not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PatchMapping("/password")
    public ResponseEntity<Void> changePassword(
            @Valid @RequestBody ChangePasswordRequest changePasswordRequest
    ) {
        log.info("Password change requested");
        ChangePasswordCommand changePasswordCommand = changePasswordMapper.toCommand(changePasswordRequest);
        changePasswordUseCase.changePassword(changePasswordCommand);
        log.info("Password changed successfully");
        return ResponseEntity.ok().build();
    }
}
