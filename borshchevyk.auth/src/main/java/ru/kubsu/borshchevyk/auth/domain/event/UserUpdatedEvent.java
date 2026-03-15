package ru.kubsu.borshchevyk.auth.domain.event;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

/**
 * Event published when user information is updated.
 *
 * @author Aleksey Timko
 * @since 2026-03-14
 * @param userId the unique identifier of the updated user
 * @param email the updated email address of the user
 * @param tag the updated tag or display name of the user
 */
@Slf4j
@Builder
public record UserUpdatedEvent(
        UUID userId,
        String email,
        String tag
) {}
