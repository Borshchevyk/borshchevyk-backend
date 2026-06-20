package ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.request;
import ru.kubsu.borshchevyk.message.domain.exception.*;

import java.util.UUID;


public record CreatePrivateChatRequest(
        UUID targetUserId
) {
}
