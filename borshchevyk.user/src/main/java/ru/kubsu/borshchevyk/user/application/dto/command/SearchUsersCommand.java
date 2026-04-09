package ru.kubsu.borshchevyk.user.application.dto.command;

import lombok.Builder;

public record SearchUsersCommand(
    String query,
    String requesterId
) {
    @Builder
    public SearchUsersCommand {}
}
