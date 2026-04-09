package ru.kubsu.borshchevyk.user.application.dto.command;

import lombok.Builder;

public record LoadContactsCommand(
    String ownerId
) {
    @Builder
    public LoadContactsCommand {}
}
