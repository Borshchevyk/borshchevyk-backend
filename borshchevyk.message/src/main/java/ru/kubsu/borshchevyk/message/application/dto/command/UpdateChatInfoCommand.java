package ru.kubsu.borshchevyk.message.application.dto.command;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UpdateChatInfoCommand(
    UUID chatId,
    UUID requesterId,
    String title,
    String description,
    Boolean commentsEnabled
) { }