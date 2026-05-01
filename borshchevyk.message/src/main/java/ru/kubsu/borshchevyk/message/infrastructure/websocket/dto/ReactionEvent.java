package ru.kubsu.borshchevyk.message.infrastructure.websocket.dto;

import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response.ShortUserDto;
import java.util.UUID;

/**
 * @author Aleksey Timko
 */
public record ReactionEvent(
        UUID messageId,
        ShortUserDto user,
        String reaction,
        boolean isAdded
) {
}
