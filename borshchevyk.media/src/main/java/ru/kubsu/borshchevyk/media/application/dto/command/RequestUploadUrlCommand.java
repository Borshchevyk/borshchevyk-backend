package ru.kubsu.borshchevyk.media.application.dto.command;

import lombok.Builder;
import ru.kubsu.borshchevyk.media.domain.model.AttachmentType;

import java.util.UUID;

/**
 * Command to request an upload URL (e.g., pre-signed S3 URL) for an attachment.
 *
 * @author Aleksey Timko
 */
@Builder
public record RequestUploadUrlCommand(
        UUID uploaderId,
        AttachmentType type,
        String contentType,
        String originalFilename,
        String extension,
        Long sizeBytes,
        Integer width,
        Integer height,
        Double duration
) {
}
