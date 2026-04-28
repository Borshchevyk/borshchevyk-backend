package ru.kubsu.borshchevyk.auth.infrastructure.adapter.in.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Request to change user password.
 *
 * @param email User's email address
 * @param oldPassword User's old password hash
 * @param newPassword User's new password hash
 * @author Aleksey Timko
 * @since 2026-03-14
 */
@Schema(description = "Request to change user password")
public record ChangePasswordRequest(
        @Email(message = "Invalid email format")
        @NotBlank(message = "Email is mandatory")
        @Schema(description = "User's email address", example = "user@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
        String email,

        @NotBlank(message = "Old password is mandatory")
        @Schema(description = "User's old password hash", example = "e3b0c442...", requiredMode = Schema.RequiredMode.REQUIRED)
        String oldPassword,

        @NotBlank(message = "New password is mandatory")
        @Schema(description = "User's new password hash", example = "e3b0c442...", requiredMode = Schema.RequiredMode.REQUIRED)
        String newPassword
) { }
