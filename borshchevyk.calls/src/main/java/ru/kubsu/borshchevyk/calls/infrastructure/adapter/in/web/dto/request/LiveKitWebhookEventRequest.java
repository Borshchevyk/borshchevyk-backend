package ru.kubsu.borshchevyk.calls.infrastructure.adapter.in.web.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO for receiving webhook events from LiveKit.
 * 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "LiveKit webhook event payload")
public record LiveKitWebhookEventRequest(
        @Schema(description = "Type of the event", example = "participant_joined")
        String event,
        
        @Schema(description = "Room information")
        RoomDto room,
        
        @Schema(description = "Participant information")
        ParticipantDto participant
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Schema(description = "Room information inside webhook")
    public record RoomDto(
            @Schema(description = "Name of the room", example = "room-123")
            String name
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Schema(description = "Participant information inside webhook")
    public record ParticipantDto(
            @Schema(description = "Identity of the participant", example = "user-123")
            String identity
    ) {}
}
