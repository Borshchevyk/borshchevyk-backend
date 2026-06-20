package ru.kubsu.borshchevyk.media.application.dto.response;

import lombok.Builder;

/**
 * Result containing the resolved URL of an attachment.
 *
 * @author Aleksey Timko
 */
@Builder
public record AttachmentUrlResult(
        String url
) {
}
