package ru.kubsu.borshchevyk.message.application.port.in;

import ru.kubsu.borshchevyk.message.application.dto.command.PinChatCommand;

public interface PinChatUseCase {
    void pinChat(PinChatCommand command);
}