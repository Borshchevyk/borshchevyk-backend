package ru.kubsu.borshchevyk.user.domain.event;

import lombok.Builder;

import java.util.UUID;

/**
 * Domain event published when a user's details are updated.
 *
 * @param userId the unique identifier of the updated user
 * @param email  the updated email address
 * @param tag    the updated user tag
 * @author Aleksey Timko
 * @since 2026-03-15
 */
@Builder
public record UserUpdatedEvent(
        UUID userId,
        String email,
        String tag,
        String firstName,
        String lastName,
        String bio,
        String avatarUrl,
        java.util.List<String> avatars
) {}
