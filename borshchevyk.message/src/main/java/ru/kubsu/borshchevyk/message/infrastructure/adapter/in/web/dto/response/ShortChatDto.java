package ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response;

import lombok.Builder;
import java.util.UUID;

/**
 * @author Aleksey Timko
 * @since 2026-05-01
 */
@Builder
public record ShortChatDto(
        UUID id,
        String title
) {
}
