package ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.kubsu.borshchevyk.message.domain.model.message.MessageSource;

import java.util.List;
import java.util.UUID;

/**
 * @author Aleksey Timko
 * @since 2026-05-01
 */
@Schema(description = "Request to send a new message")
public record SendMessageRequest(
        @Schema(description = "Text of the message", example = "Hello, world!", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        String text,

        @Schema(description = "Source of the message (ONLINE/OFFLINE)", example = "ONLINE", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        MessageSource source,

        @Schema(description = "ID of the original chat if forwarded", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        UUID forwardedFromChatId,

        @Schema(description = "ID of the original user if forwarded", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        UUID forwardedFromUserId,

        @Schema(description = "ID of the parent message if this is a comment or reply", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        UUID parentMessageId,

        @Schema(description = "List of attachment IDs", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        List<UUID> attachmentIds
) {
}
