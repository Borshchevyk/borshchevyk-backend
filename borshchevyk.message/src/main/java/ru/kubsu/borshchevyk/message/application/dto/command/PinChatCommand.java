package ru.kubsu.borshchevyk.message.application.dto.command;

import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class PinChatCommand {
    UUID requesterId;
    UUID chatId;
}
