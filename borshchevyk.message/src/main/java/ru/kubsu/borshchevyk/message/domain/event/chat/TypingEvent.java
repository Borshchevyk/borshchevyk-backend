package ru.kubsu.borshchevyk.message.domain.event.chat;

import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response.ShortUserDto;

import java.util.UUID;

public record TypingEvent(
        UUID chatId,
        ShortUserDto user,
        boolean isTyping
) { }