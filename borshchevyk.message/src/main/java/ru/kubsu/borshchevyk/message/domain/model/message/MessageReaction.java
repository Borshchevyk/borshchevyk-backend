package ru.kubsu.borshchevyk.message.domain.model.message;

import java.util.UUID;

/**
 * Domain model representing a reaction to a message by a user.
 *
 * @author Aleksey Timko
 */
public record MessageReaction(
        UUID userId,
        String reaction
) {
}
