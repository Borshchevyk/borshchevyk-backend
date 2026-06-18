package ru.kubsu.borshchevyk.message.application.dto.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record AttachmentMetadataResponse(
    UUID id,
    String type,
    String originalFilename,
    String extension,
    Long sizeBytes,
    Double duration,
    UUID thumbnailId
) { }