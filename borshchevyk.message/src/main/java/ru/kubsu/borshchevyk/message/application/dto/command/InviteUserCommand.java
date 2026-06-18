package ru.kubsu.borshchevyk.message.application.dto.command;

import lombok.Builder;

import java.util.UUID;

@Builder
public record InviteUserCommand(
    UUID chatId,
    UUID requesterId,
    UUID targetUserId
) { }