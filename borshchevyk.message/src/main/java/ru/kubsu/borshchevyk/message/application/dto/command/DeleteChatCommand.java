package ru.kubsu.borshchevyk.message.application.dto.command;

import lombok.Builder;

import java.util.UUID;

@Builder
public record DeleteChatCommand(
    UUID chatId,
    UUID requesterId
) { }