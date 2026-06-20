package ru.kubsu.borshchevyk.media.application.dto.response;

import lombok.Builder;
import java.util.UUID;

/**
 * Result containing the upload URL (e.g., presigned) and related attachment tracking information.
 *
 * @author Aleksey Timko
 */
@Builder
public record UploadUrlResult(
        UUID attachmentId,
        String uploadUrl,
        String s3Key
) {
}
