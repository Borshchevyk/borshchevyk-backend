package ru.kubsu.borshchevyk.message.domain.event.reaction;

import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response.ShortUserDto;

import java.util.UUID;

public record ReactionEvent(
        UUID messageId,
        ShortUserDto user,
        String reaction,
        boolean isAdded
) { }