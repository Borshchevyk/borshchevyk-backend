package ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatType;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Chat details response")
public record ChatResponse(
        @Schema(description = "Unique identifier of the chat")
        UUID id,
        @Schema(description = "Type of the chat")
        ChatType type,
        @Schema(description = "Title of the chat")
        String title,
        @Schema(description = "Description of the chat")
        String description,
        @Schema(description = "Timestamp when the chat was created")
        LocalDateTime createdAt
) {
}
