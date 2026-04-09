package ru.kubsu.borshchevyk.user.application.dto.command;

import lombok.Builder;

public record UpdateProfileCommand(
    String userId,
    String firstName,
    String lastName,
    String bio,
    String avatarUrl
) {
    @Builder
    public UpdateProfileCommand {}
}
