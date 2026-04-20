package ru.kubsu.borshchevyk.message.infrastructure.websocket.dto;

import java.util.Set;
import java.util.UUID;

public record ChatSettingsEvent(
        UUID chatId,
        Set<String> allowedReactions
) {
}
