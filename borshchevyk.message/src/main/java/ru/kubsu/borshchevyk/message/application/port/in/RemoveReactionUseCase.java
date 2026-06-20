package ru.kubsu.borshchevyk.message.application.port.in;

import ru.kubsu.borshchevyk.message.application.dto.command.RemoveReactionCommand;

public interface RemoveReactionUseCase {
    void removeReaction(RemoveReactionCommand command);
}