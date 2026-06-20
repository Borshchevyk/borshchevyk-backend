package ru.kubsu.borshchevyk.media.application.port.in;

import ru.kubsu.borshchevyk.media.application.dto.command.RequestUploadUrlCommand;
import ru.kubsu.borshchevyk.media.application.dto.response.UploadUrlResult;

/**
 * UseCase to request an S3 pre-signed upload URL.
 * Validates constraints and generates upload url.
 *
 * @author Aleksey Timko
 */
public interface RequestUploadUrlUseCase {
    UploadUrlResult requestUploadUrl(RequestUploadUrlCommand command);
}
