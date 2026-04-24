package ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response;

import lombok.Builder;
import java.util.UUID;

@Builder
public record EnrichedUserResponse(
        UUID id,
        String firstName,
        String lastName,
        String tag,
        String avatarUrl
) {
}
