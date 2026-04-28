package ru.kubsu.borshchevyk.media.infrastructure.adapter.in.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.kubsu.borshchevyk.media.domain.model.AttachmentType;

@Schema(description = "Request to get a pre-signed URL for direct S3 upload")
public record RequestUploadUrlRequest(
        @Schema(description = "Type of the attachment", example = "PHOTO", requiredMode = Schema.RequiredMode.REQUIRED)
        AttachmentType type,
        
        @Schema(description = "MIME type of the file", example = "image/jpeg", requiredMode = Schema.RequiredMode.REQUIRED)
        String contentType,
        
        @Schema(description = "Original name of the file", example = "vacation.jpg", requiredMode = Schema.RequiredMode.REQUIRED)
        String originalFilename,
        
        @Schema(description = "File extension without dot", example = "jpg", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        String extension,
        
        @Schema(description = "Size of the file in bytes", example = "1048576", requiredMode = Schema.RequiredMode.REQUIRED)
        Long sizeBytes,
        
        @Schema(description = "Width of the image/video in pixels", example = "1920", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        Integer width,
        
        @Schema(description = "Height of the image/video in pixels", example = "1080", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        Integer height,
        
        @Schema(description = "Duration of the media in seconds", example = "15.5", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        Double duration
) {
}
