package ru.kubsu.borshchevyk.message.domain.model.message;

import java.util.UUID;

public record MessageReaction(
        UUID userId,
        String reaction
) {
}
