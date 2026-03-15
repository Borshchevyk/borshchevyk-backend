package ru.kubsu.borshchevyk.auth.domain.event;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

/**
 * Event published when a new user is registered.
 *
 * @author Aleksey Timko
 * @since 2026-03-14
 * @param userId the unique identifier of the registered user
 * @param email the email address of the registered user
 * @param tag the user's tag or display name
 */
@Slf4j
@Builder
public record UserRegisteredEvent(
        UUID userId,
        String email,
        String tag
) {}
