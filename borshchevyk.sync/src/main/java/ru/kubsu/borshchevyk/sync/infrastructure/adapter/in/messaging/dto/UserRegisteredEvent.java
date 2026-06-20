package ru.kubsu.borshchevyk.sync.infrastructure.adapter.in.messaging.dto;

/**
 * Event received when a user registers.
 *
 * @author Aleksey Timko
 */
public record UserRegisteredEvent(
        String userId,
        String email,
        String tag,
        String firstName
) {}
