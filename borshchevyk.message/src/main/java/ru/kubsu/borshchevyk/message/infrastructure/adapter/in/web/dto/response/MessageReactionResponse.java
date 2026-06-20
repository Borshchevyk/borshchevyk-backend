package ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response;
import ru.kubsu.borshchevyk.message.domain.exception.*;

import java.util.UUID;


public record MessageReactionResponse(
        UUID userId,
        String reaction
) {
}
