package ru.kubsu.borshchevyk.message.domain.model.value;

import java.util.UUID;

/**
 * Value object representing the unique identifier of a Chat.
 *
 * @author Aleksey Timko
 */
public record ChatId(UUID value) {
}
