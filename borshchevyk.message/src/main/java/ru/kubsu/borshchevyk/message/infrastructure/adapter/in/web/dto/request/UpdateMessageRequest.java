package ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.request;
import ru.kubsu.borshchevyk.message.domain.exception.*;

import io.swagger.v3.oas.annotations.media.Schema;


@Schema(description = "Request to update an existing message")
public record UpdateMessageRequest(
        @Schema(description = "New text of the message")
        String text
) {}
