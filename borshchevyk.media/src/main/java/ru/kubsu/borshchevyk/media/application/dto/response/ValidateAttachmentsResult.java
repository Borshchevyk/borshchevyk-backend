package ru.kubsu.borshchevyk.media.application.dto.response;

import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.UUID;

@Value
@Builder
public class ValidateAttachmentsResult {
    boolean valid;
    List<AttachmentMetadata> attachments;

    @Value
    @Builder
    public static class AttachmentMetadata {
        UUID id;
        String type;
        String originalFilename;
        String extension;
        Long sizeBytes;
        Double duration;
    }
}
