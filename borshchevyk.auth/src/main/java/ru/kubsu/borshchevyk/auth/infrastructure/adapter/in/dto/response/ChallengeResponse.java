package ru.kubsu.borshchevyk.auth.infrastructure.adapter.in.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response containing the cryptographic challenge")
public class ChallengeResponse {
    @Schema(description = "The generated challenge string to be signed", example = "550e8400-e29b-41d4-a716-446655440000")
    private String challenge;
}
