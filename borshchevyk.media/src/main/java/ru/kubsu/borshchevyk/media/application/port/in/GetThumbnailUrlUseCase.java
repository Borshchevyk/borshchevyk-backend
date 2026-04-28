package ru.kubsu.borshchevyk.media.application.port.in;

import ru.kubsu.borshchevyk.media.application.dto.command.GetAttachmentUrlCommand;
import ru.kubsu.borshchevyk.media.application.dto.response.AttachmentUrlResult;

public interface GetThumbnailUrlUseCase {
    AttachmentUrlResult getThumbnailUrl(GetAttachmentUrlCommand command);
}
