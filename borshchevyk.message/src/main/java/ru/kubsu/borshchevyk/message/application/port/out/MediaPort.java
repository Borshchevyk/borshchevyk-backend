package ru.kubsu.borshchevyk.message.application.port.out;

import ru.kubsu.borshchevyk.message.application.dto.response.AttachmentMetadataDto;

import java.util.List;
import java.util.UUID;

/**
 * Output port for interacting with the media service.
 *
 * @author Aleksey Timko
 */
public interface MediaPort {
    List<AttachmentMetadataDto> validateAttachments(List<UUID> attachmentIds, UUID userId);
}
