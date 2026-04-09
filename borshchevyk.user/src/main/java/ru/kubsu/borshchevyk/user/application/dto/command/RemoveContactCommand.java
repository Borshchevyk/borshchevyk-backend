package ru.kubsu.borshchevyk.user.application.dto.command;

import lombok.Builder;

public record RemoveContactCommand(
    String ownerId,
    String targetUserId
) {
    @Builder
    public RemoveContactCommand {}
}
