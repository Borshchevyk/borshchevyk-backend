package ru.kubsu.borshchevyk.message.domain.event.chat;

import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response.ShortUserDto;

public record TypingEvent(
        ShortUserDto user,
        boolean isTyping
) { }