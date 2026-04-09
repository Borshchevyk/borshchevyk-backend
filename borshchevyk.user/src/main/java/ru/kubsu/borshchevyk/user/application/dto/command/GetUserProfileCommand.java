package ru.kubsu.borshchevyk.user.application.dto.command;

import lombok.Builder;

public record GetUserProfileCommand(
    String targetUserIdOrTag,
    String requesterId
) {
    @Builder
    public GetUserProfileCommand {}
}
