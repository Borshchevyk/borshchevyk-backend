package ru.kubsu.borshchevyk.message.application.dto.command;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class LeaveChatCommand {
    private UUID chatId;
    private UUID requesterId;
}
