package ru.kubsu.borshchevyk.media.application.dto.command;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

/**
 * Command to validate the existence and ownership of multiple attachments.
 *
 * @author Aleksey Timko
 */
@Builder
public record ValidateAttachmentsCommand(
        List<UUID> attachmentIds,
        UUID requesterId
) {
}
