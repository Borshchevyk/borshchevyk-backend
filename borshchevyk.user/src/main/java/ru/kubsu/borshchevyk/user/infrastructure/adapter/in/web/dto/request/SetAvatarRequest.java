package ru.kubsu.borshchevyk.user.infrastructure.adapter.in.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record SetAvatarRequest(
    @Schema(description = "URL to the user's avatar image", example = "https://example.com/avatar.jpg")
    String avatarUrl
) {}
