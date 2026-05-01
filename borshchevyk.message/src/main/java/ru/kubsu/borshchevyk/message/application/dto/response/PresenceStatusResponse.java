package ru.kubsu.borshchevyk.message.application.dto.response;

import lombok.Builder;
import java.util.UUID;

/**
 * DTO representing user presence status.
 *
 * @author Aleksey Timko
 */
@Builder
public record PresenceStatusResponse(
    UUID userId,
    boolean isOnline,
    Long lastSeenAt
) {
}
