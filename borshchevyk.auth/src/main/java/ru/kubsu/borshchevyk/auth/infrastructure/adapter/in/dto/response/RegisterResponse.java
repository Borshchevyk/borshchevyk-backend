package ru.kubsu.borshchevyk.auth.infrastructure.adapter.in.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import java.util.UUID;

@Builder
@Schema(description = "Successful registration response")
public record RegisterResponse(
        @Schema(description = "UUID of the newly created user", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID userId
) { }
