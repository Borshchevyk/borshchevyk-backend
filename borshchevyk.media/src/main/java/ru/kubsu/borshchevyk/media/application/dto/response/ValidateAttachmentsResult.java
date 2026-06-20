package ru.kubsu.borshchevyk.media.application.dto.response;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

/**
 * Result of validating attachments.
 *
 * @author Aleksey Timko
 */
@Builder
public record ValidateAttachmentsResult(
        boolean valid,
        List<AttachmentMetadata> attachments
) {
    /**
     * Metadata of a validated attachment.
     */
    @Builder
    public record AttachmentMetadata(
            UUID id,
            String type,
            String originalFilename,
            String extension,
            Long sizeBytes,
            Double duration,
            UUID thumbnailId
    ) {
    }
}
