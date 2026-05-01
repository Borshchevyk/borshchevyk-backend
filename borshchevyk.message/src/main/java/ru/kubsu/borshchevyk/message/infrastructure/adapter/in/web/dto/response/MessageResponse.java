package ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.kubsu.borshchevyk.message.domain.model.message.MessageSource;
import ru.kubsu.borshchevyk.message.domain.model.message.MessageStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * @author Aleksey Timko
 * @since 2026-05-01
 */
@Schema(description = "Message details response")
public record MessageResponse(
        @Schema(description = "Unique identifier of the message")
        UUID id,
        @Schema(description = "Information about the chat")
        ShortChatDto chat,
        @Schema(description = "Information about the author")
        ShortUserDto author,
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
        @Schema(description = "Information about the chat this message was forwarded from")
        ShortChatDto forwardedFromChat,
        @Schema(description = "Information about the user this message was forwarded from")
        ShortUserDto forwardedFromUser,
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
            Long sizeBytes,
            @Schema(description = "Duration of the attachment in seconds (for VOICE and CIRCLE)")
            Double duration,
            @Schema(description = "ID of the thumbnail attachment (optional)")
            UUID thumbnailId
    ) {}
}
