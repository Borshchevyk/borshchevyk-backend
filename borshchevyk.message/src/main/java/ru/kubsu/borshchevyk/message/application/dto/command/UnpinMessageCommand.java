package ru.kubsu.borshchevyk.message.application.dto.command;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UnpinMessageCommand {
    private UUID chatId;
    private UUID messageId;
    private UUID requesterId;
}
