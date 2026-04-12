package ru.kubsu.borshchevyk.media.application.port.in;

import ru.kubsu.borshchevyk.media.application.dto.command.ValidateAttachmentsCommand;
import ru.kubsu.borshchevyk.media.application.dto.response.ValidateAttachmentsResult;

public interface ValidateAttachmentsUseCase {
    ValidateAttachmentsResult validateAttachments(ValidateAttachmentsCommand command);
}
