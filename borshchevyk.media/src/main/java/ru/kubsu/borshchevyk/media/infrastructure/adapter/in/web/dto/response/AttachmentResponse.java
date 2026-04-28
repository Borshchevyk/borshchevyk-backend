package ru.kubsu.borshchevyk.media.infrastructure.adapter.in.web.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.kubsu.borshchevyk.media.domain.model.AttachmentStatus;
import ru.kubsu.borshchevyk.media.domain.model.AttachmentType;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Response containing full attachment metadata")
public record AttachmentResponse(
        @Schema(description = "Unique ID of the attachment", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,
        
        @Schema(description = "ID of the user who uploaded the attachment", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID uploaderId,
        
        @Schema(description = "Type of the attachment", example = "PHOTO")
        AttachmentType type,
        
        @Schema(description = "S3 Object Key", example = "attachments/user-id/uuid.jpg")
        String s3Key,
        
        @Schema(description = "S3 Object Key for the thumbnail", example = "thumbnails/user-id/uuid.jpg")
        String thumbnailKey,
        
        @Schema(description = "ID of the thumbnail attachment (same as thumbnail key's UUID)", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID thumbnailId,
        
        @Schema(description = "Original filename", example = "image.png")
        String originalFilename,
        
        @Schema(description = "File extension", example = "png")
        String extension,
        
        @Schema(description = "MIME content type", example = "image/png")
        String contentType,
        
        @Schema(description = "Size in bytes", example = "2048")
        Long sizeBytes,
        
        @Schema(description = "Width in pixels", example = "800")
        Integer width,
        
        @Schema(description = "Height in pixels", example = "600")
        Integer height,
        
        @Schema(description = "Duration in seconds", example = "120.5")
        Double duration,
        
        @Schema(description = "Current status of the attachment", example = "READY")
        AttachmentStatus status,
        
        @Schema(description = "Time of creation", example = "2026-04-12T10:15:30")
        LocalDateTime createdAt,
        
        @Schema(description = "Time of last update", example = "2026-04-12T10:15:30")
        LocalDateTime updatedAt
) {
}
