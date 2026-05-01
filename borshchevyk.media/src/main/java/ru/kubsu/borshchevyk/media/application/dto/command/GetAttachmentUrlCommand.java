package ru.kubsu.borshchevyk.media.application.dto.command;

import lombok.Builder;

import java.util.UUID;

/**
 * Command to request the URL of an existing attachment.
 *
 * @author Aleksey Timko
 */
@Builder
public record GetAttachmentUrlCommand(
        UUID requesterId,
        UUID attachmentId
) {
}
