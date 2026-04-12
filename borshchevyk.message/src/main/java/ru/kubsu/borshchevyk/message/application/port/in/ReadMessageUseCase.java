package ru.kubsu.borshchevyk.message.application.port.in;

import ru.kubsu.borshchevyk.message.application.dto.command.ReadMessageCommand;

public interface ReadMessageUseCase {
    void readMessage(ReadMessageCommand command);
}
