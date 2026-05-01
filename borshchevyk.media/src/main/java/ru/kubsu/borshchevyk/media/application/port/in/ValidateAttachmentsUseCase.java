package ru.kubsu.borshchevyk.media.application.port.in;

import ru.kubsu.borshchevyk.media.application.dto.command.ValidateAttachmentsCommand;
import ru.kubsu.borshchevyk.media.application.dto.response.ValidateAttachmentsResult;

/**
 * UseCase to validate that attachments exist and belong to the correct user.
 * Used internally before attaching media to messages or calls.
 *
 * @author Aleksey Timko
 */
public interface ValidateAttachmentsUseCase {
    ValidateAttachmentsResult validateAttachments(ValidateAttachmentsCommand command);
}
