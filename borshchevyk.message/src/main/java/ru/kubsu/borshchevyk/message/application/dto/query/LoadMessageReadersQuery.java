package ru.kubsu.borshchevyk.message.application.dto.query;

import java.util.UUID;

public record LoadMessageReadersQuery(
        UUID chatId,
        UUID messageId,
        UUID requesterId
) { }