package ru.kubsu.borshchevyk.message.infrastructure.websocket.dto;

import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response.ShortUserDto;
import java.util.UUID;

public record ReadReceiptEvent(
        ShortUserDto user,
        UUID messageId
) {
}
