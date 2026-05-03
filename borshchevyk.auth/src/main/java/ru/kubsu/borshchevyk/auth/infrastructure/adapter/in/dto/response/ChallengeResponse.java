package ru.kubsu.borshchevyk.auth.infrastructure.adapter.in.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

/**
 * Response containing the cryptographic challenge.
 *
 * @param challenge The generated challenge string to be signed
 */
@Builder
@Schema(description = "Response containing the cryptographic challenge")
public record ChallengeResponse(
        @Schema(
                description = "The generated challenge string to be signed",
                example = "550e8400-e29b-41d4-a716-446655440000"
        )
        String challenge
) { }
