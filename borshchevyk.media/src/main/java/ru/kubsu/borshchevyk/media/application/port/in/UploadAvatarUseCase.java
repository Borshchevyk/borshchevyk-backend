package ru.kubsu.borshchevyk.media.application.port.in;

import ru.kubsu.borshchevyk.media.application.dto.command.UploadAvatarCommand;
import ru.kubsu.borshchevyk.media.application.dto.response.AttachmentUrlResult;

/**
 * UseCase for uploading a user avatar directly.
 * Handles the logic of replacing old avatars and returning the new URL.
 *
 * @author Aleksey Timko
 */
public interface UploadAvatarUseCase {
    AttachmentUrlResult uploadAvatar(UploadAvatarCommand command);
}
