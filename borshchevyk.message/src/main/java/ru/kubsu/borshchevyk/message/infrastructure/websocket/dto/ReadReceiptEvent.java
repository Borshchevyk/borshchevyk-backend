package ru.kubsu.borshchevyk.message.infrastructure.websocket.dto;

import java.util.UUID;

public record ReadReceiptEvent(
        UUID userId,
        UUID messageId
) {
}
