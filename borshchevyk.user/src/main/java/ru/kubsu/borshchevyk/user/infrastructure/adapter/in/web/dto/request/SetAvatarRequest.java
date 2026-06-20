package ru.kubsu.borshchevyk.user.infrastructure.adapter.in.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Request DTO for setting the user's main avatar.
 *
 * @author Aleksey Timko
 */
public record SetAvatarRequest(
    @Schema(description = "URL to the user's avatar image", example = "https://example.com/avatar.jpg")
    String avatarUrl
) {}