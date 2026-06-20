package ru.kubsu.borshchevyk.message.domain.event.message;

import java.util.List;
import java.util.UUID;

public record MessageDeletedEvent(
        UUID messageId,
        UUID chatId,
        List<String> targetUserIds
) { }