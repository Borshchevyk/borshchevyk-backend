package ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response;

import lombok.Builder;
import java.util.UUID;

@Builder
public record ShortChatDto(
        UUID id,
        String title
) {
}
