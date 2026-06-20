package ru.kubsu.borshchevyk.message.application.dto.command;

import lombok.Builder;

import java.util.UUID;

@Builder
public record PinMessageCommand(
    UUID chatId,
    UUID messageId,
    UUID requesterId
) { }
