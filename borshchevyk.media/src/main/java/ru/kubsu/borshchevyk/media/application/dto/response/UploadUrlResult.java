package ru.kubsu.borshchevyk.media.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadUrlResult {
    private UUID attachmentId;
    private String uploadUrl;
    private String s3Key;
}
