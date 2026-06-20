package ru.kubsu.borshchevyk.media.application.port.in;

import ru.kubsu.borshchevyk.media.application.dto.command.CompleteUploadCommand;
import ru.kubsu.borshchevyk.media.domain.model.Attachment;

/**
 * UseCase for completing a pre-signed S3 upload process.
 * Marks the attachment as active.
 *
 * @author Aleksey Timko
 */
public interface CompleteUploadUseCase {
    Attachment completeUpload(CompleteUploadCommand command);
}
