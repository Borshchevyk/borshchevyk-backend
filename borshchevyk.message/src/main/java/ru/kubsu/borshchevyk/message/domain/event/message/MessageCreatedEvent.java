package ru.kubsu.borshchevyk.message.domain.event.message;

import ru.kubsu.borshchevyk.message.domain.model.message.MessageStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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
    public record AttachmentInfo(
            UUID id,
            String type,
            String originalFilename,
            String extension,
            Long sizeBytes,
            Double duration
    ) { }
}