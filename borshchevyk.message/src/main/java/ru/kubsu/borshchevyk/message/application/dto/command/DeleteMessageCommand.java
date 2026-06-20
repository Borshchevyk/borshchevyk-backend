package ru.kubsu.borshchevyk.message.application.dto.command;

import lombok.Builder;

import java.util.UUID;

@Builder
public record DeleteMessageCommand(
    UUID messageId,
    UUID requesterId,
    boolean forAll,
    boolean isSyncMutation
) { 
    public DeleteMessageCommand(UUID messageId, UUID requesterId, boolean forAll) {
        this(messageId, requesterId, forAll, false);
    }
}