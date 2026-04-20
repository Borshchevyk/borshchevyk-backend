package ru.kubsu.borshchevyk.message.infrastructure.websocket.dto;

import java.util.UUID;

public record ChatMemberEvent(
        UUID chatId,
        UUID userId,
        String action
) {
}
