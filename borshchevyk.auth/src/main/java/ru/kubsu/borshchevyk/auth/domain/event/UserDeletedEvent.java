package ru.kubsu.borshchevyk.auth.domain.event;

import lombok.Builder;
import java.util.UUID;

/**
 * Event published when a user is deleted.
 *
 * @param userId the unique identifier of the deleted user
 * @author Aleksey Timko
 */
@Builder
public record UserDeletedEvent(
        UUID userId
) {}