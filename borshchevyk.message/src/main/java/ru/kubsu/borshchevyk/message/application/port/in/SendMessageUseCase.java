package ru.kubsu.borshchevyk.message.application.port.in;

import ru.kubsu.borshchevyk.message.application.dto.command.SendMessageCommand;
import ru.kubsu.borshchevyk.message.domain.model.message.Message;

public interface SendMessageUseCase {
    Message sendMessage(SendMessageCommand command);
}
