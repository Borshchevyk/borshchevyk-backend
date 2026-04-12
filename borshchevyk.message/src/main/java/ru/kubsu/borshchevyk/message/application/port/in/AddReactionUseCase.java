package ru.kubsu.borshchevyk.message.application.port.in;

import ru.kubsu.borshchevyk.message.application.dto.command.AddReactionCommand;

public interface AddReactionUseCase {
    void addReaction(AddReactionCommand command);
}
