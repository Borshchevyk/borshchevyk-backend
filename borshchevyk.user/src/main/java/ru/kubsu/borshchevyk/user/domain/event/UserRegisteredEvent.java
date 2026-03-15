package ru.kubsu.borshchevyk.user.domain.event;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UserRegisteredEvent(
        UUID userId,
        String email,
        String tag
) {}
