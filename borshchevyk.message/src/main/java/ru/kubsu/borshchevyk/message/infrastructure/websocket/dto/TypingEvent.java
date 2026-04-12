package ru.kubsu.borshchevyk.message.infrastructure.websocket.dto;

import java.util.UUID;

public record TypingEvent(
        UUID userId,
        boolean isTyping
) {
}
