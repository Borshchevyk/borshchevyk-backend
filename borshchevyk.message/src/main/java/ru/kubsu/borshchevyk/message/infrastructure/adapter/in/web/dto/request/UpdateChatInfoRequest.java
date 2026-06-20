package ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.request;
import ru.kubsu.borshchevyk.message.domain.exception.*;


public record UpdateChatInfoRequest(
        String title,
        String description,
        Boolean commentsEnabled
) {
}
