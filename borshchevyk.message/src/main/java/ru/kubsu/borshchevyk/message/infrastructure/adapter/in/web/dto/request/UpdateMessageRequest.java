package ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author Aleksey Timko
 * @since 2026-05-01
 */
@Schema(description = "Request to update an existing message")
public record UpdateMessageRequest(
        @Schema(description = "New text of the message")
        String text
) {}