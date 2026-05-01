package ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatType;

import java.util.List;
import java.util.UUID;

/**
 * @author Aleksey Timko
 * @since 2026-05-01
 */
@Schema(description = "Request to create a new chat")
public record CreateChatRequest(
        @NotNull(message = "Chat type is required")
        @Schema(description = "Type of the chat", example = "PRIVATE")
        ChatType type,
        @Schema(description = "Title of the chat (for GROUP chats)", example = "My awesome group")
        String title,
        @Schema(description = "Description of the chat", example = "Group for friends")
        String description,
        @Schema(description = "Initial members of the chat", example = "[\"123e4567-e89b-12d3-a456-426614174000\"]")
        List<UUID> initialMemberIds,
        @Schema(description = "Whether comments are enabled (for CHANNEL type)", example = "true")
        Boolean commentsEnabled
) {
}
