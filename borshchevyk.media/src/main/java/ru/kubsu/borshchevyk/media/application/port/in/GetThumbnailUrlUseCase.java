package ru.kubsu.borshchevyk.media.application.port.in;

import ru.kubsu.borshchevyk.media.application.dto.command.GetAttachmentUrlCommand;
import ru.kubsu.borshchevyk.media.application.dto.response.AttachmentUrlResult;

/**
 * UseCase for retrieving the thumbnail URL for a video or image.
 * Checks permissions and generates the corresponding S3 pre-signed URL.
 *
 * @author Aleksey Timko
 */
public interface GetThumbnailUrlUseCase {
    AttachmentUrlResult getThumbnailUrl(GetAttachmentUrlCommand command);
}
