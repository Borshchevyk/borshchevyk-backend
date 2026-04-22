package ru.kubsu.borshchevyk.media.application.dto.response;

import lombok.Builder;
import lombok.Value;
import java.util.UUID;

@Value
@Builder
public class UploadUrlResult {
    UUID attachmentId;
    String uploadUrl;
    String s3Key;
}
