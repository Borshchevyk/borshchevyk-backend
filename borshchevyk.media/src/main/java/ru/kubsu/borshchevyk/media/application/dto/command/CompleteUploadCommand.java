package ru.kubsu.borshchevyk.media.application.dto.command;

import lombok.Builder;

import java.util.UUID;

/**
 * Command for completing an upload process.
 *
 * @author Aleksey Timko
 */
@Builder
public record CompleteUploadCommand(
        UUID attachmentId,
        UUID requesterId
) {
}
