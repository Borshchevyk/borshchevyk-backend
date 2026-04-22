package ru.kubsu.borshchevyk.message.application.port.out;

import ru.kubsu.borshchevyk.message.application.dto.response.AttachmentMetadataDto;

import java.util.List;
import java.util.UUID;

public interface MediaPort {
    List<AttachmentMetadataDto> validateAttachments(List<UUID> attachmentIds, UUID userId);
}
