package ru.kubsu.borshchevyk.user.infrastructure.adapter.in.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Request DTO for updating the user's profile information.
 *
 * @author Aleksey Timko
 */
public record UpdateProfileRequest(
    @Schema(description = "User's first name", example = "Ivan")
    String firstName,
    @Schema(description = "User's last name", example = "Ivanov")
    String lastName,
    @Schema(description = "User's biography", example = "Software Engineer")
    String bio,
    @Schema(description = "URL to the user's avatar image", example = "https://example.com/avatar.jpg")
    String avatarUrl
) {}