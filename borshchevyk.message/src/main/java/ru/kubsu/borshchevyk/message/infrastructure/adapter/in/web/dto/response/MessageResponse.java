package ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.kubsu.borshchevyk.message.domain.model.message.MessageSource;

import java.time.LocalDateTime;
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
        MessageSource source
) {
}
