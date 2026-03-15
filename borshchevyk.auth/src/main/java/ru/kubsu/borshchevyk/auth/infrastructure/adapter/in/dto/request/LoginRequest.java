package ru.kubsu.borshchevyk.auth.infrastructure.adapter.in.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request to login an existing user")
public record LoginRequest(
        @Schema(description = "User's email address", example = "user@example.com")
        String email,
        @Schema(description = "Client-side AuthHash (not the raw password)", example = "e3b0c442...")
        String passwordHash
) { }
