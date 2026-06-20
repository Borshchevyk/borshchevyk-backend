package ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response;
import ru.kubsu.borshchevyk.message.domain.exception.*;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;


public record GlobalSearchResponse(
        @Schema(description = "List of found chats")
        List<ChatResponse> chats,
        @Schema(description = "List of found users")
        List<ShortUserDto> users
) {
}
