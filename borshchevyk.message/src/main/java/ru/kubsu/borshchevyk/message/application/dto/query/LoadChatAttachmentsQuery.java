package ru.kubsu.borshchevyk.message.application.dto.query;

import java.util.UUID;

public record LoadChatAttachmentsQuery(
        UUID chatId,
        UUID userId,
        String type,
        int page,
        int size
) { }