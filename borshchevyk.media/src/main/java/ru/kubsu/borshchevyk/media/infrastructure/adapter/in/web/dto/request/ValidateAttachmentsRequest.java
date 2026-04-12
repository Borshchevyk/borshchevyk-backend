package ru.kubsu.borshchevyk.media.infrastructure.adapter.in.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;

@Schema(description = "Request to validate attachments")
public record ValidateAttachmentsRequest(
        @Schema(description = "List of attachment IDs to validate", requiredMode = Schema.RequiredMode.REQUIRED)
        List<UUID> attachmentIds
) {
}
