package ru.kubsu.borshchevyk.message.application.dto.command;

import java.util.UUID;

public record CreatePrivateChatCommand(
        UUID requesterId,
        UUID targetUserId
) { }