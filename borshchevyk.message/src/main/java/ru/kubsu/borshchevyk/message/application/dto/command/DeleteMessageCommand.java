package ru.kubsu.borshchevyk.message.application.dto.command;

import lombok.Builder;
import java.util.UUID;

/**
 * Command to delete a message.
 *
 * @author Aleksey Timko
 */
@Builder
public record DeleteMessageCommand(
    UUID messageId,
    UUID requesterId,
    boolean forAll
) {
}
