package ru.kubsu.borshchevyk.message.application.dto.response;

import lombok.Builder;
import java.util.UUID;

/**
 * DTO representing attachment metadata.
 *
 * @author Aleksey Timko
 */
@Builder
public record AttachmentMetadataDto(
    UUID id,
    String type,
    String originalFilename,
    String extension,
    Long sizeBytes,
    Double duration,
    UUID thumbnailId
) {
}
