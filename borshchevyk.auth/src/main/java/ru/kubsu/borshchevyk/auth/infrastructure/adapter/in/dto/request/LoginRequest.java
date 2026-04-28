package ru.kubsu.borshchevyk.auth.infrastructure.adapter.in.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Request to login an existing user.
 *
 * @param email        User's email address
 * @param passwordHash Client-side AuthHash (not the raw password)
 * @author Aleksey Timko
 * @since 2026-03-14
 */
@Schema(description = "Request to login an existing user")
public record LoginRequest(
        @Email(message = "Invalid email format")
        @NotBlank(message = "Email is mandatory")
        @Schema(description = "User's email address", example = "user@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
        String email,
        @NotBlank(message = "Password hash is mandatory")
        @Schema(description = "Client-side AuthHash (not the raw password)", example = "e3b0c442...", requiredMode = Schema.RequiredMode.REQUIRED)
        String passwordHash
) { }
