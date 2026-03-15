package ru.kubsu.borshchevyk.user.domain.event;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UserDeletedEvent(
        UUID userId
) {}
