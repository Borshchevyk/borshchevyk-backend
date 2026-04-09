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
public class DeleteMessageCommand {
    private UUID messageId;
    private UUID requesterId;
    private boolean forAll;
}