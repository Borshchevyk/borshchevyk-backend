package ru.kubsu.borshchevyk.media.application.port.in;

import ru.kubsu.borshchevyk.media.application.dto.command.UploadAttachmentCommand;
import ru.kubsu.borshchevyk.media.domain.model.Attachment;

public interface UploadAttachmentUseCase {
    Attachment uploadAttachment(UploadAttachmentCommand command);
}
