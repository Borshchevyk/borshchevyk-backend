package ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request to send a new message")
public record SendMessageRequest(
        @Schema(description = "Text of the message", example = "Hello, world!", requiredMode = Schema.RequiredMode.REQUIRED)
        String text
) {
}
