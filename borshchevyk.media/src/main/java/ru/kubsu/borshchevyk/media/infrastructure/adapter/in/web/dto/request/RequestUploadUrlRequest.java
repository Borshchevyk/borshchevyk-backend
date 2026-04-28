package ru.kubsu.borshchevyk.media.infrastructure.adapter.in.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import ru.kubsu.borshchevyk.media.domain.model.AttachmentType;

/**
 * DTO for requesting a pre-signed upload URL.
 *
 * @author Aleksey Timko
 */
@Schema(description = "Request to get a pre-signed URL for direct S3 upload")
public record RequestUploadUrlRequest(
        @NotNull
        @Schema(description = "Type of the attachment", example = "PHOTO", requiredMode = Schema.RequiredMode.REQUIRED)
        AttachmentType type,
        
        @NotBlank
        @Schema(description = "MIME type of the file", example = "image/jpeg", requiredMode = Schema.RequiredMode.REQUIRED)
        String contentType,
        
        @NotBlank
        @Schema(description = "Original name of the file", example = "vacation.jpg", requiredMode = Schema.RequiredMode.REQUIRED)
        String originalFilename,
        
        @Schema(description = "File extension without dot", example = "jpg", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        String extension,
        
        @NotNull
        @Positive
        @Schema(description = "Size of the file in bytes", example = "1048576", requiredMode = Schema.RequiredMode.REQUIRED)
        Long sizeBytes,
        
        @Positive
        @Schema(description = "Width of the image/video in pixels", example = "1920", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        Integer width,
        
        @Positive
        @Schema(description = "Height of the image/video in pixels", example = "1080", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        Integer height,
        
        @Positive
        @Schema(description = "Duration of the media in seconds", example = "15.5", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        Double duration
) {
}
