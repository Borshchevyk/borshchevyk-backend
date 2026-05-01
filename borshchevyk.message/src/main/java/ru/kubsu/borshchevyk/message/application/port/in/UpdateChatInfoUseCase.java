package ru.kubsu.borshchevyk.message.application.port.in;

import ru.kubsu.borshchevyk.message.application.dto.command.UpdateChatInfoCommand;

/**
 * UseCase for updating chat information (title, description, etc.).
 *
 * @author Aleksey Timko
 */
public interface UpdateChatInfoUseCase {
    void updateChatInfo(UpdateChatInfoCommand command);
}
