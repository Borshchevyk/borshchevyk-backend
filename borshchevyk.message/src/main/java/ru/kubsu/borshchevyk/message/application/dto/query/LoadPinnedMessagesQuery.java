package ru.kubsu.borshchevyk.message.application.dto.query;

import java.util.UUID;

public record LoadPinnedMessagesQuery(
        UUID chatId,
        UUID requesterId
) { }