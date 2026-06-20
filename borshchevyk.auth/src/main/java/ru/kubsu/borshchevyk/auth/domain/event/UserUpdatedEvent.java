package ru.kubsu.borshchevyk.auth.domain.event;

import lombok.Builder;

import java.util.UUID;

/**
 * Event published when user information is updated.
 *
 * @param userId the unique identifier of the updated user
 * @param email  the updated email address of the user
 * @param tag    the updated tag or display name of the user
 */
@Builder
public record UserUpdatedEvent(
        UUID userId,
        String email,
        String tag
) { }