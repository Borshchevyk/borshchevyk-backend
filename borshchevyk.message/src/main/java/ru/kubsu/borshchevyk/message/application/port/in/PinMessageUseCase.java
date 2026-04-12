package ru.kubsu.borshchevyk.message.application.port.in;

import ru.kubsu.borshchevyk.message.application.dto.command.PinMessageCommand;

public interface PinMessageUseCase {
    void pinMessage(PinMessageCommand command);
}
