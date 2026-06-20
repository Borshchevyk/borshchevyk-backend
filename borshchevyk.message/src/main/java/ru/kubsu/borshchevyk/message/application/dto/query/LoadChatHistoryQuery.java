package ru.kubsu.borshchevyk.message.application.dto.query;

import java.util.UUID;

public record LoadChatHistoryQuery(
        UUID chatId,
        UUID userId,
        int page,
        int size
) { }