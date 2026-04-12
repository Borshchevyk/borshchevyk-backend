package ru.kubsu.borshchevyk.message.infrastructure.websocket.dto;

import java.util.UUID;

public record ReactionEvent(
        UUID messageId,
        UUID userId,
        String reaction,
        boolean isAdded
) {
}
