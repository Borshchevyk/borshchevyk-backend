package ru.kubsu.borshchevyk.calls.infrastructure.adapter.in.web.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import java.util.UUID;

/**
 * DTO for short user information obtained via gRPC.
 *
 * @author Aleksey Timko
 * @since 2026-04-25
 */
@Builder
@Schema(description = "Short user information")
public record ShortUserDto(
        @Schema(description = "Unique user ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,
        
        @Schema(description = "User's first name", requiredMode = Schema.RequiredMode.REQUIRED, example = "John")
        String firstName,
        
        @Schema(description = "User's last name", requiredMode = Schema.RequiredMode.REQUIRED, example = "Doe")
        String lastName,
        
        @Schema(description = "User's unique tag", requiredMode = Schema.RequiredMode.REQUIRED, example = "johndoe123")
        String tag,
        
        @Schema(description = "URL to the user's avatar", example = "https://example.com/avatar.jpg")
        String avatarUrl
) {
}
