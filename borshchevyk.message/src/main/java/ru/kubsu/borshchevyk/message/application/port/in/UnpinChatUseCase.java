package ru.kubsu.borshchevyk.message.application.port.in;

import ru.kubsu.borshchevyk.message.application.dto.command.UnpinChatCommand;

public interface UnpinChatUseCase {
    void unpinChat(UnpinChatCommand command);
}
