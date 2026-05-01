package ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response;

import java.util.UUID;

/**
 * @author Aleksey Timko
 */
public record MessageReactionResponse(
        UUID userId,
        String reaction
) {
}
