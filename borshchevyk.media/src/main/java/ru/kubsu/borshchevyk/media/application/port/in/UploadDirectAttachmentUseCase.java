package ru.kubsu.borshchevyk.media.application.port.in;

import ru.kubsu.borshchevyk.media.application.dto.command.UploadDirectAttachmentCommand;
import ru.kubsu.borshchevyk.media.domain.model.Attachment;

/**
 * UseCase to upload an attachment directly via the backend API.
 * This skips pre-signed URLs and uploads stream directly to S3.
 *
 * @author Aleksey Timko
 */
public interface UploadDirectAttachmentUseCase {
    Attachment uploadDirectAttachment(UploadDirectAttachmentCommand command);
}
