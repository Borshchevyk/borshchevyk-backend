package ru.kubsu.borshchevyk.message.infrastructure.websocket.dto;

import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response.ShortChatDto;

/**
 * @author Aleksey Timko
 */
public record ChatEvent(
        ShortChatDto chat,
        String action
) {
}
