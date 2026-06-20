package ru.kubsu.borshchevyk.sync.infrastructure.adapter.in.messaging.dto;

import java.util.List;

/**
 * Event received when a new message is created.
 *
 * @author Aleksey Timko
 */
public record MessageCreatedEvent(
        String id,
        String chatId,
        String authorId,
        String text,
        String createdAt,
        String status,
        List<String> targetUserIds,
        List<AttachmentInfo> attachments
) {
    public record AttachmentInfo(
            String id,
            String type,
            String originalFilename,
            String extension,
            Long sizeBytes,
            Double duration
    ) {}
}
