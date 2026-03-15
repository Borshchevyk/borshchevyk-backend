package ru.kubsu.borshchevyk.auth.domain.event;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UserDeletedEvent(
        UUID userId
) {}
