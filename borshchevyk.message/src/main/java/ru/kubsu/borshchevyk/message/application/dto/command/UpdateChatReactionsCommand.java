package ru.kubsu.borshchevyk.message.application.dto.command;

import lombok.Builder;

import java.util.Set;
import java.util.UUID;

@Builder
public record UpdateChatReactionsCommand(
    UUID chatId,
    UUID requesterId,
    Set<String> allowedReactions
) { }