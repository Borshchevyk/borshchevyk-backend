package ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.kubsu.borshchevyk.message.domain.model.message.MessageSource;

@Schema(description = "Request to send a new message")
public record SendMessageRequest(
        @Schema(description = "Text of the message", example = "Hello, world!", requiredMode = Schema.RequiredMode.REQUIRED)
        String text,
        
        @Schema(description = "Source of the message (ONLINE/OFFLINE)", example = "ONLINE", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        MessageSource source
) {
}
