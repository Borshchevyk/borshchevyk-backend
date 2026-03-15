package ru.kubsu.borshchevyk.user.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Data Transfer Object representing the updated user profile.
 *
 * @author Aleksey Timko
 * @since 2026-03-15
 */
@Data
@Schema(description = "Response object containing updated user profile details")
public class EditUserResponse {

    @Schema(description = "UUID of the user", example = "550e8400-e29b-41d4-a716-446655440000")
    private String userId;

    @Schema(description = "User's current email address", example = "user@example.com")
    private String email;

    @Schema(description = "User's current unique tag", example = "user_tag")
    private String tag;
}
