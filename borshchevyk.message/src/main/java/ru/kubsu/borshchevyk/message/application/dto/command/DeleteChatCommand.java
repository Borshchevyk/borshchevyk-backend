package ru.kubsu.borshchevyk.message.application.dto.command;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class DeleteChatCommand {
    private final UUID chatId;
    private final UUID requesterId;
}
