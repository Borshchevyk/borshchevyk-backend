package ru.kubsu.borshchevyk.user.infrastructure.adapter.in.web.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * Class documentation.
 *
 * @author Aleksey Timko
 */
public record UserProfileResponse(
    @Schema(description = "Unique identifier of the user", example = "550e8400-e29b-41d4-a716-446655440000")
    String userId,
    @Schema(description = "Validated email address", example = "ivan@example.com")
    String email,
    @Schema(description = "Unique display tag", example = "ivan_ivanov")
    String tag,
    @Schema(description = "User's first name", example = "Ivan")
    String firstName,
    @Schema(description = "User's last name", example = "Ivanov")
    String lastName,
    @Schema(description = "User's biography", example = "Software Engineer")
    String bio,
    @Schema(description = "URL to the user's avatar image", example = "https://example.com/avatar.jpg")
    String avatarUrl,
    @Schema(description = "List of user's avatar images")
    List<String> avatars
) {}

