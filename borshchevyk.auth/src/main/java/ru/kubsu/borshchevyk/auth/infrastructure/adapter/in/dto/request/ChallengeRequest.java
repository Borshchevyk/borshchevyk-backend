package ru.kubsu.borshchevyk.auth.infrastructure.adapter.in.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

/**
 * Request to generate a cryptographic challenge.
 *
 * @param userId UUID of the user requesting the challenge
 * @author Aleksey Timko
 * @since 2026-03-14
 */
@Schema(description = "Request to generate a cryptographic challenge")
public record ChallengeRequest(
        @Schema(description = "UUID of the user requesting the challenge", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID userId
) { }
