package ru.kubsu.borshchevyk.message.application.dto.command;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UpdatePermissionsCommand(
    UUID chatId,
    UUID targetUserId,
    UUID requesterId,
    Boolean canSendMessages,
    Boolean canDeleteMessages,
    Boolean canInviteUsers,
    Boolean canChangeInfo
) { }