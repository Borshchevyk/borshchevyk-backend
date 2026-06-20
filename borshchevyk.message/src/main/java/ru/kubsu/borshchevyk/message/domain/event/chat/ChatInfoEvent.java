package ru.kubsu.borshchevyk.message.domain.event.chat;

import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response.ShortChatDto;

public record ChatInfoEvent(
        ShortChatDto chat,
        String description,
        Boolean commentsEnabled
) { }