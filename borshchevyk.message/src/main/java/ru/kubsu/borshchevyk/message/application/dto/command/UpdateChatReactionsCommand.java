package ru.kubsu.borshchevyk.message.application.dto.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateChatReactionsCommand {
    private UUID chatId;
    private UUID requesterId;
    private Set<String> allowedReactions;
}