package ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.request;

public record UpdateChatInfoRequest(
        String title,
        String description
) {
}
