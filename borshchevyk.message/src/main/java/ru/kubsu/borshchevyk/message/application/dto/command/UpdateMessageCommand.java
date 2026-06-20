package ru.kubsu.borshchevyk.message.application.dto.command;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UpdateMessageCommand(
    UUID messageId,
    UUID chatId,
    UUID requesterId,
    String text,
    boolean isSyncMutation
) { 
    public UpdateMessageCommand(UUID messageId, UUID chatId, UUID requesterId, String text) {
        this(messageId, chatId, requesterId, text, false);
    }
}