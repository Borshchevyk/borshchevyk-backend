package ru.kubsu.borshchevyk.media.infrastructure.adapter.in.web.dto.response;

import ru.kubsu.borshchevyk.media.domain.model.AttachmentStatus;
import ru.kubsu.borshchevyk.media.domain.model.AttachmentType;

import java.time.LocalDateTime;
import java.util.UUID;

public record AttachmentResponse(
        UUID id,
        UUID uploaderId,
        AttachmentType type,
        String s3Key,
        String thumbnailKey,
        UUID thumbnailId,
        String originalFilename,
        String extension,
        String contentType,
        Long sizeBytes,
        Integer width,
        Integer height,
        Double duration,
        AttachmentStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
