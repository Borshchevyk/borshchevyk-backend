package ru.kubsu.borshchevyk.message.infrastructure.websocket.dto;

import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response.ShortUserDto;

/**
 * @author Aleksey Timko
 */
public record TypingEvent(
        ShortUserDto user,
        boolean isTyping
) {
}
