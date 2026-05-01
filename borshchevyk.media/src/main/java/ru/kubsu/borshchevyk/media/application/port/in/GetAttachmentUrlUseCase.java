package ru.kubsu.borshchevyk.media.application.port.in;

import ru.kubsu.borshchevyk.media.application.dto.command.GetAttachmentUrlCommand;
import ru.kubsu.borshchevyk.media.application.dto.response.AttachmentUrlResult;

/**
 * UseCase for retrieving a download URL for a specific attachment.
 * Checks permissions if needed and generates pre-signed URL for read.
 *
 * @author Aleksey Timko
 */
public interface GetAttachmentUrlUseCase {
    AttachmentUrlResult getAttachmentUrl(GetAttachmentUrlCommand command);
}
