package ru.kubsu.borshchevyk.message.application.port.out;

import ru.kubsu.borshchevyk.message.application.dto.response.AttachmentMetadataResponse;

import java.util.List;
import java.util.UUID;

public interface ValidateAttachmentsPort {
    List<AttachmentMetadataResponse> validateAttachments(List<UUID> attachmentIds, UUID userId);
}