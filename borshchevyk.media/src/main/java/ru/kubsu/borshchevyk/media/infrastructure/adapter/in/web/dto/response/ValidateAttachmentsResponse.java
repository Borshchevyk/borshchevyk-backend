package ru.kubsu.borshchevyk.media.infrastructure.adapter.in.web.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response for attachment validation")
public record ValidateAttachmentsResponse(
        @Schema(description = "Indicates whether all requested attachments are valid")
        boolean valid
) {
}
