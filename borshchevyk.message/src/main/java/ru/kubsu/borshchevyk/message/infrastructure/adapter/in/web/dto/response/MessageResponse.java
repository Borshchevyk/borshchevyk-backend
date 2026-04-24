package ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.kubsu.borshchevyk.message.domain.model.message.MessageSource;
import ru.kubsu.borshchevyk.message.domain.model.message.MessageStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Schema(description = "Message details response")
public record MessageResponse(
        @Schema(description = "Unique identifier of the message")
        UUID id,
        @Schema(description = "Unique identifier of the chat")
        UUID chatId,
        @Schema(description = "Unique identifier of the author")
        UUID authorId,
        @Schema(description = "Name of the author")
        String authorName,
        @Schema(description = "Avatar URL of the author")
        String authorAvatarUrl,
        @Schema(description = "Text of the message")
        String text,
        @Schema(description = "Timestamp when the message was sent")
        LocalDateTime createdAt,
        @Schema(description = "Timestamp when the message was updated")
        LocalDateTime updatedAt,
        @Schema(description = "Indicates if the message is deleted")
        boolean isDeleted,
        @Schema(description = "Source of the message")
        MessageSource source,
        @Schema(description = "Status of the message")
        MessageStatus status,
        @Schema(description = "Timestamp when the message was pinned")
        LocalDateTime pinnedAt,
        @Schema(description = "User who pinned the message")
        UUID pinnedBy,
        @Schema(description = "ID of the chat this message was forwarded from")
        UUID forwardedFromChatId,
        @Schema(description = "ID of the user this message was forwarded from")
        UUID forwardedFromUserId,
        @Schema(description = "ID of the parent message if this is a comment")
        UUID parentMessageId,
        @Schema(description = "Number of comments on this message")
        int commentsCount,
        @Schema(description = "List of attachments with metadata")
        List<AttachmentResponse> attachments
) {
    @Schema(description = "Attachment metadata inside a message")
    public record AttachmentResponse(
            @Schema(description = "ID of the attachment")
            UUID id,
            @Schema(description = "Type of the attachment")
            String type,
            @Schema(description = "Original filename of the attachment")
            String originalFilename,
            @Schema(description = "Extension of the attachment")
            String extension,
            @Schema(description = "Size of the attachment in bytes")
            Long sizeBytes
    ) {}
}
