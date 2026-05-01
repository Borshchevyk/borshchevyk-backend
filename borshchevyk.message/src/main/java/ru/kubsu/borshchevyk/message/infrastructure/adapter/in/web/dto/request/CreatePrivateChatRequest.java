package ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.request;

import java.util.UUID;

/**
 * @author Aleksey Timko
 */
public record CreatePrivateChatRequest(
        UUID targetUserId
) {
}
