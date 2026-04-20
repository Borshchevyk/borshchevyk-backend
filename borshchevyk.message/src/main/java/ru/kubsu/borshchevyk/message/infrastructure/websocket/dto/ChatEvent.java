package ru.kubsu.borshchevyk.message.infrastructure.websocket.dto;

import java.util.UUID;

public record ChatEvent(
        UUID chatId,
        String action
) {
}
