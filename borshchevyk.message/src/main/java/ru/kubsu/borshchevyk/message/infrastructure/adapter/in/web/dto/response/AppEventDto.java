package ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppEventDto {
    /**
     * Type of the event (e.g., MESSAGE_CREATED, CHAT_UPDATED, TYPING, PRESENCE, SYNC_EVENT).
     * This tells the client how to parse the payload.
     */
    private String eventType;

    /**
     * The actual event payload. Can be mapped from any domain event or DTO.
     */
    private Object payload;
}
