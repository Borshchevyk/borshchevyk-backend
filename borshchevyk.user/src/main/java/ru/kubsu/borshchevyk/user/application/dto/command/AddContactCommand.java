package ru.kubsu.borshchevyk.user.application.dto.command;

import lombok.Builder;

public record AddContactCommand(
    String ownerId,
    String targetUserId,
    String firstName,
    String lastName
) {
    @Builder
    public AddContactCommand {}
}
