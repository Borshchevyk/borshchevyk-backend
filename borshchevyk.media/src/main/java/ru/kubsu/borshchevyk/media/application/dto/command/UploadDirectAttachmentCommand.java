package ru.kubsu.borshchevyk.media.application.dto.command;

import lombok.Builder;
import ru.kubsu.borshchevyk.media.domain.model.AttachmentType;

import java.io.InputStream;
import java.util.UUID;

/**
 * Command to upload an attachment directly via the backend.
 *
 * @author Aleksey Timko
 */
@Builder
public record UploadDirectAttachmentCommand(
        UUID uploaderId,
        InputStream inputStream,
        String contentType,
        Long sizeBytes,
        String extension,
        String originalFilename,
        AttachmentType type,
        Double duration
) {
}
