package ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.kubsu.borshchevyk.message.domain.model.message.MessageSource;

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
        @Schema(description = "Text of the message")
        String text,
        @Schema(description = "Timestamp when the message was sent")
        LocalDateTime createdAt,
        @Schema(description = "Indicates if the message is deleted")
        boolean isDeleted,
        @Schema(description = "Source of the message")
        MessageSource source,
        @Schema(description = "Timestamp when the message was pinned")
        LocalDateTime pinnedAt,
        @Schema(description = "Identifier of the user who pinned the message")
        UUID pinnedBy,
        @Schema(description = "Reactions on the message")
        List<MessageReactionResponse> reactions,
        @Schema(description = "ID of the original chat if forwarded")
        UUID forwardedFromChatId,
        @Schema(description = "ID of the original user if forwarded")
        UUID forwardedFromUserId,
        @Schema(description = "ID of the parent message if this is a comment")
        UUID parentMessageId,
        @Schema(description = "Number of comments on this message")
        int commentsCount,
        @Schema(description = "List of attachment IDs")
        List<UUID> attachmentIds
) {
}
