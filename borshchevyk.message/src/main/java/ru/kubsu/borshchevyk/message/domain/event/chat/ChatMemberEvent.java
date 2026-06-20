package ru.kubsu.borshchevyk.message.domain.event.chat;

import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response.ShortChatDto;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response.ShortUserDto;

public record ChatMemberEvent(
        ShortChatDto chat,
        ShortUserDto user,
        String action
) { }