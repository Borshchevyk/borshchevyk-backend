package ru.kubsu.borshchevyk.message.infrastructure.websocket.dto;

import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response.ShortChatDto;
import java.util.Set;

public record ChatSettingsEvent(
        ShortChatDto chat,
        Set<String> allowedReactions
) {
}
