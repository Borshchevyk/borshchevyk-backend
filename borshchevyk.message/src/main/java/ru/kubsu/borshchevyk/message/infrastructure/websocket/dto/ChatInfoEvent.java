package ru.kubsu.borshchevyk.message.infrastructure.websocket.dto;

import java.util.UUID;

public record ChatInfoEvent(
        UUID chatId,
        String title,
        String description,
        Boolean commentsEnabled
) {
}
