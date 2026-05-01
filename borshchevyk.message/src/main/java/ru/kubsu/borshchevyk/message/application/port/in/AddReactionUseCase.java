package ru.kubsu.borshchevyk.message.application.port.in;

import ru.kubsu.borshchevyk.message.application.dto.command.AddReactionCommand;

/**
 * UseCase for adding a reaction to a message.
 *
 * @author Aleksey Timko
 */
public interface AddReactionUseCase {
    void addReaction(AddReactionCommand command);
}
