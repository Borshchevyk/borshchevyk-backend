package ru.kubsu.borshchevyk.message.application.dto.command;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UpdateChatInfoCommand {
    private UUID chatId;
    private UUID requesterId;
    private String title;
    private String description;
    private Boolean commentsEnabled;
}
