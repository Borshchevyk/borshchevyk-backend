package ru.kubsu.borshchevyk.message.application.dto.query;

import java.util.UUID;

public record LoadMessageCommentsQuery(
        UUID chatId,
        UUID parentMessageId,
        UUID requesterId,
        int page,
        int size
) { }