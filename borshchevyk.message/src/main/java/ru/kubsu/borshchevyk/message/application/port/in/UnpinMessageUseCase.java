package ru.kubsu.borshchevyk.message.application.port.in;

import ru.kubsu.borshchevyk.message.application.dto.command.UnpinMessageCommand;

public interface UnpinMessageUseCase {
    void unpinMessage(UnpinMessageCommand command);
}