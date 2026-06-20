package ru.kubsu.borshchevyk.message.domain.event.message;

import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response.ShortUserDto;

import java.util.UUID;

public record ReadMessageEvent(
        UUID chatId,
        ShortUserDto user,
        UUID messageId
) { }