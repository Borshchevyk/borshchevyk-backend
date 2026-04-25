package ru.kubsu.borshchevyk.media.application.dto.command;

import lombok.Builder;
import lombok.Value;

import java.util.UUID;
import java.io.InputStream;

@Value
@Builder
public class UploadAvatarCommand {
    UUID uploaderId;
    InputStream inputStream;
    String contentType;
    long sizeBytes;
    String extension;
    String originalFilename;
}
