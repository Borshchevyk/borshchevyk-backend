package ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response;
import ru.kubsu.borshchevyk.message.domain.exception.*;

import lombok.Builder;
import java.util.UUID;


@Builder
public record ShortUserDto(
        UUID id,
        String firstName,
        String lastName,
        String tag,
        String avatarUrl
) {
}
