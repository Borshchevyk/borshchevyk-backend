package ru.kubsu.borshchevyk.auth.domain.event;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

/**
 * Event published when a user is deleted.
 *
 * @author Aleksey Timko
 * @since 2026-03-14
 * @param userId the unique identifier of the deleted user
 */
@Slf4j
@Builder
public record UserDeletedEvent(
        UUID userId
) {}
