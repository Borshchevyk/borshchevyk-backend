package ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response;
import ru.kubsu.borshchevyk.message.domain.exception.*;

import lombok.Builder;
import java.util.UUID;


@Builder
public record ShortChatDto(
        UUID id,
        String title
) {
}
