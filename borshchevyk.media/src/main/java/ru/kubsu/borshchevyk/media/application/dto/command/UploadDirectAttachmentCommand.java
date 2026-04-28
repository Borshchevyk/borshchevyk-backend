package ru.kubsu.borshchevyk.media.application.dto.command;

import lombok.Builder;
import lombok.Value;
import ru.kubsu.borshchevyk.media.domain.model.AttachmentType;

import java.io.InputStream;
import java.util.UUID;

@Value
@Builder
public class UploadDirectAttachmentCommand {
    UUID uploaderId;
    InputStream inputStream;
    String contentType;
    Long sizeBytes;
    String extension;
    String originalFilename;
    AttachmentType type;
    Double duration;
}