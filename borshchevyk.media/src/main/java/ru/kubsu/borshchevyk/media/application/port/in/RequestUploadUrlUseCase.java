package ru.kubsu.borshchevyk.media.application.port.in;

import ru.kubsu.borshchevyk.media.application.dto.command.RequestUploadUrlCommand;
import ru.kubsu.borshchevyk.media.application.dto.response.UploadUrlResult;

public interface RequestUploadUrlUseCase {
    UploadUrlResult requestUploadUrl(RequestUploadUrlCommand command);
}
