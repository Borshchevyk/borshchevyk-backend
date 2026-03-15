package ru.kubsu.borshchevyk.auth.domain.event;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UserUpdatedEvent(
        UUID userId,
        String email,
        String tag
) {}
