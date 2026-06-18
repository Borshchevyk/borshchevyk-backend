package ru.kubsu.borshchevyk.message.application.port.in;

import ru.kubsu.borshchevyk.message.application.dto.command.UpdateMessageCommand;
import ru.kubsu.borshchevyk.message.domain.model.message.Message;

public interface UpdateMessageUseCase {
    Message updateMessage(UpdateMessageCommand command);
}