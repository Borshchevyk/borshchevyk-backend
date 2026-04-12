package ru.kubsu.borshchevyk.message.domain.event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record MessageCreatedEvent(
        UUID id,
        UUID chatId,
        UUID authorId,
        String text,
        LocalDateTime createdAt,
        List<String> targetUserIds,
        List<UUID> attachmentIds
) {
}
