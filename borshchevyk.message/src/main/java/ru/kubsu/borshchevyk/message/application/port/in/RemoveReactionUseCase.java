package ru.kubsu.borshchevyk.message.application.port.in;

import ru.kubsu.borshchevyk.message.application.dto.command.RemoveReactionCommand;

/**
 * UseCase for removing a reaction from a message.
 *
 * @author Aleksey Timko
 */
public interface RemoveReactionUseCase {
    void removeReaction(RemoveReactionCommand command);
}
