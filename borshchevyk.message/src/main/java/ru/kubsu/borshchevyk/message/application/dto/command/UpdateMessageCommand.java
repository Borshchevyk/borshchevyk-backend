package ru.kubsu.borshchevyk.message.application.dto.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMessageCommand {
    private UUID messageId;
    private UUID chatId;
    private UUID requesterId;
    private String text;
}