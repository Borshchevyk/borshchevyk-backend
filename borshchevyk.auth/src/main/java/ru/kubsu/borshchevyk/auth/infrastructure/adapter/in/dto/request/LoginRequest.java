package ru.kubsu.borshchevyk.auth.infrastructure.adapter.in.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

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
        @Schema(description = "User's email address", example = "user@example.com")
        String email,
        @Schema(description = "Client-side AuthHash (not the raw password)", example = "e3b0c442...")
        String passwordHash
) { }
