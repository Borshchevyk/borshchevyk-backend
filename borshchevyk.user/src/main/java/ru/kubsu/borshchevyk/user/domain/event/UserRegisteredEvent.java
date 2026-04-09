package ru.kubsu.borshchevyk.user.domain.event;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

/**
 * Domain event published when a new user is registered in the system.
 *
 * @param userId the unique identifier assigned to the new user
 * @param email  the email address used for registration
 * @param tag    the user tag chosen during registration
 * @author Aleksey Timko
 * @since 2026-03-15
 */
@Slf4j
@Builder
public record UserRegisteredEvent(
        UUID userId,
        String email,
        String tag,
        String firstName
) {}
