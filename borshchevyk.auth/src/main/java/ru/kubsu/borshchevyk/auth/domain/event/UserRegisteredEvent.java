package ru.kubsu.borshchevyk.auth.domain.event;

import lombok.Builder;

import java.util.UUID;

/**
 * Event published when a new user is registered.
 *
 * @param userId    the unique identifier of the registered user
 * @param email     the email address of the registered user
 * @param tag       the user's tag or display name
 * @param firstName the user's first name
 * @param lastName  the user's last name
 */
@Builder
public record UserRegisteredEvent(
        UUID userId,
        String email,
        String tag,
        String firstName,
        String lastName
) { }