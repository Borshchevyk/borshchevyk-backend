package ru.kubsu.borshchevyk.user.domain.event;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

/**
 * Domain event published when a user is deleted from the system.
 *
 * @param userId the unique identifier of the deleted user
 * @author Aleksey Timko
 * @since 2026-03-15
 */
@Slf4j
@Builder
public record UserDeletedEvent(
        UUID userId
) {}
