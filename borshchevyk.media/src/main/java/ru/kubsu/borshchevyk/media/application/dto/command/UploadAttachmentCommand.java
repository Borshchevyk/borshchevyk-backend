package ru.kubsu.borshchevyk.media.application.dto.command;

import lombok.Builder;
import ru.kubsu.borshchevyk.media.domain.model.AttachmentType;

import java.io.InputStream;
import java.util.UUID;

/**
 * Command to upload an attachment directly via the server.
 *
 * @author Aleksey Timko
 */
@Builder
public record UploadAttachmentCommand(
        UUID uploaderId,
        AttachmentType type,
        String contentType,
        String originalFilename,
        String extension,
        Long sizeBytes,
        Integer width,
        Integer height,
        Double duration,
        InputStream inputStream
) {
}
