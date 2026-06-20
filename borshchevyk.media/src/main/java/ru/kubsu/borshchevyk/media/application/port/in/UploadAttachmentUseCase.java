package ru.kubsu.borshchevyk.media.application.port.in;

import ru.kubsu.borshchevyk.media.application.dto.command.UploadAttachmentCommand;
import ru.kubsu.borshchevyk.media.domain.model.Attachment;

/**
 * UseCase for uploading an attachment directly and immediately making it active.
 *
 * @author Aleksey Timko
 */
public interface UploadAttachmentUseCase {
    Attachment uploadAttachment(UploadAttachmentCommand command);
}
