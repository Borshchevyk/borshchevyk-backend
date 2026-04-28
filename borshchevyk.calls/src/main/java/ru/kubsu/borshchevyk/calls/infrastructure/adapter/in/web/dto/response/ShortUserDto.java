package ru.kubsu.borshchevyk.calls.infrastructure.adapter.in.web.dto.response;

import lombok.Builder;
import java.util.UUID;

/**
 * DTO for short user information obtained via gRPC.
 *
 * @author Aleksey Timko
 * @since 2026-04-25
 */
@Builder
public record ShortUserDto(
        UUID id,
        String firstName,
        String lastName,
        String tag,
        String avatarUrl
) {
}
