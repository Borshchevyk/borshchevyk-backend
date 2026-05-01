package ru.kubsu.borshchevyk.message.domain.event;

import ru.kubsu.borshchevyk.message.domain.model.message.MessageStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Domain event published when a new message is created.
 *
 * @author Aleksey Timko
 */
public record MessageCreatedEvent(
        UUID id,
        UUID chatId,
        UUID authorId,
        String text,
        LocalDateTime createdAt,
        MessageStatus status,
        List<String> targetUserIds,
        List<AttachmentInfo> attachments
) {
    /**
     * Information about a message attachment.
     *
     * @author Aleksey Timko
     */
    public record AttachmentInfo(
            UUID id,
            String type,
            String originalFilename,
            String extension,
            Long sizeBytes,
            Double duration
    ) {}
}
