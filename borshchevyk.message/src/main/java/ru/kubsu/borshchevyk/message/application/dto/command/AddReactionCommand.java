package ru.kubsu.borshchevyk.message.application.dto.command;

import lombok.Builder;

import java.util.UUID;

@Builder
public record AddReactionCommand(
    UUID chatId,
    UUID messageId,
    UUID requesterId,
    String reaction
) { }