package ru.kubsu.borshchevyk.media.infrastructure.adapter.in.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

/**
 * DTO for validating attachments.
 *
 * @author Aleksey Timko
 */
@Schema(description = "Request to validate attachments")
public record ValidateAttachmentsRequest(
        @NotNull
        @Size(min = 1, max = 50)
        @Schema(description = "List of attachment IDs to validate", requiredMode = Schema.RequiredMode.REQUIRED)
        List<UUID> attachmentIds
) {
}
