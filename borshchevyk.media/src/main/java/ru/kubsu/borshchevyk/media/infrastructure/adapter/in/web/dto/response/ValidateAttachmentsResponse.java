package ru.kubsu.borshchevyk.media.infrastructure.adapter.in.web.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;

@Schema(description = "Response for attachment validation")
public record ValidateAttachmentsResponse(
        @Schema(description = "Indicates whether all requested attachments are valid")
        boolean valid,
        
        @Schema(description = "List of validated attachments with metadata")
        List<AttachmentMetadataResponse> attachments
) {
    @Schema(description = "Metadata of a validated attachment")
    public record AttachmentMetadataResponse(
            @Schema(description = "ID of the attachment")
            UUID id,
            @Schema(description = "Type of the attachment (PHOTO, VIDEO, etc.)")
            String type,
            @Schema(description = "Original filename of the attachment")
            String originalFilename,
            @Schema(description = "Extension of the attachment")
            String extension,
            @Schema(description = "Size of the attachment in bytes")
            Long sizeBytes,
            @Schema(description = "Duration of the attachment in seconds (for VOICE and CIRCLE)")
            Double duration
    ) {
    }
}
