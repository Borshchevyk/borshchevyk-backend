package ru.kubsu.borshchevyk.media.application.port.in;

import ru.kubsu.borshchevyk.media.application.dto.command.UploadDirectAttachmentCommand;
import ru.kubsu.borshchevyk.media.domain.model.Attachment;

public interface UploadDirectAttachmentUseCase {
    Attachment uploadDirectAttachment(UploadDirectAttachmentCommand command);
}