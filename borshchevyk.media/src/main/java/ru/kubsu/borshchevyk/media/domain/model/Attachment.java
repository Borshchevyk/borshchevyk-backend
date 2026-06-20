package ru.kubsu.borshchevyk.media.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.kubsu.borshchevyk.media.domain.model.value.AttachmentId;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain entity representing a media attachment.
 *
 * @author Aleksey Timko
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attachment {
    private AttachmentId id;
    private UUID uploaderId;
    private AttachmentType type;
    private String s3Key;
    private String thumbnailKey;
    private UUID thumbnailId;
    private String originalFilename;
    private String extension;
    private String contentType;
    private Long sizeBytes;
    
    // Metadata
    private Integer width;
    private Integer height;
    private Double duration; // in seconds
    
    private AttachmentStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
