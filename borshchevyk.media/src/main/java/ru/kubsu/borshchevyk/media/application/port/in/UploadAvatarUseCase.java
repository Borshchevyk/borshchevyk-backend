package ru.kubsu.borshchevyk.media.application.port.in;

import ru.kubsu.borshchevyk.media.application.dto.command.UploadAvatarCommand;
import ru.kubsu.borshchevyk.media.application.dto.response.AttachmentUrlResult;

public interface UploadAvatarUseCase {
    AttachmentUrlResult uploadAvatar(UploadAvatarCommand command);
}
