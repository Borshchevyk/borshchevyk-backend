package ru.kubsu.borshchevyk.message.domain.model.value;

import java.util.UUID;

/**
 * Value object representing the unique identifier of a Message.
 *
 * @author Aleksey Timko
 */
public record MessageId(UUID value) {
}
