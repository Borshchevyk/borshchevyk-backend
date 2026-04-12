package ru.kubsu.borshchevyk.media.application.dto.command;

import lombok.Builder;
import lombok.Data;
import ru.kubsu.borshchevyk.media.domain.model.AttachmentType;

import java.util.UUID;

@Data
@Builder
public class RequestUploadUrlCommand {
    private UUID uploaderId;
    private AttachmentType type;
    private String contentType;
    private String originalFilename;
    private String extension;
    private Long sizeBytes;
}
