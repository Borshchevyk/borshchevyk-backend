package ru.kubsu.borshchevyk.media.infrastructure.adapter.in.web.dto.request;

import ru.kubsu.borshchevyk.media.domain.model.AttachmentType;

public record RequestUploadUrlRequest(
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
