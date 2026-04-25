package ru.kubsu.borshchevyk.calls.infrastructure.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

/**
 * Response DTO representing a Call.
 *
 * @author Gemini
 * @since 2026-04-25
 */
@Schema(description = "Response object representing a Call")
public record CallResponse(
        @Schema(description = "Unique call ID")
        UUID id,
        
        @Schema(description = "LiveKit room ID mapping")
        String roomId,
        
        @Schema(description = "ID of the user who initiated the call")
        UUID initiatorId,
        
        @Schema(description = "Current status of the call")
        String status,
        
        @Schema(description = "When the call was created")
        Instant createdAt,
        
        @Schema(description = "When the call ended, if applicable")
        Instant endedAt,
        
        @Schema(description = "Set of participant user IDs")
        Set<UUID> participants
) {}
