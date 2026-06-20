package ru.kubsu.borshchevyk.media.application.dto.command;

import lombok.Builder;

import java.io.InputStream;
import java.util.UUID;

/**
 * Command for uploading a user avatar directly.
 *
 * @author Aleksey Timko
 */
@Builder
public record UploadAvatarCommand(
        UUID uploaderId,
        InputStream inputStream,
        String contentType,
        long sizeBytes,
        String extension,
        String originalFilename
) {
}
