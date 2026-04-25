package ru.kubsu.borshchevyk.message.infrastructure.websocket.dto;

import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response.ShortUserDto;

public record TypingEvent(
        ShortUserDto user,
        boolean isTyping
) {
}
