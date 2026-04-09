package ru.kubsu.borshchevyk.message.application.port.in;

import ru.kubsu.borshchevyk.message.application.dto.command.DeleteMessageCommand;

public interface DeleteMessageUseCase {
    void deleteMessage(DeleteMessageCommand command);
}