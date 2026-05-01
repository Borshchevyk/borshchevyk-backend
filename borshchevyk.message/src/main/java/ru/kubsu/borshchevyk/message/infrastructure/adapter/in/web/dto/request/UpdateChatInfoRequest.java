package ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.request;

/**
 * @author Aleksey Timko
 */
public record UpdateChatInfoRequest(
        String title,
        String description,
        Boolean commentsEnabled
) {
}
