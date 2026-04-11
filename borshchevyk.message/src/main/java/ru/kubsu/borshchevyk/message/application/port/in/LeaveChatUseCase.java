package ru.kubsu.borshchevyk.message.application.port.in;

import ru.kubsu.borshchevyk.message.application.dto.command.LeaveChatCommand;

public interface LeaveChatUseCase {
    void leaveChat(LeaveChatCommand command);
}
