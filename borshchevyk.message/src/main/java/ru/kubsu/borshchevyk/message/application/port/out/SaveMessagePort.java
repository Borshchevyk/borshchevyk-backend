package ru.kubsu.borshchevyk.message.application.port.out;

import ru.kubsu.borshchevyk.message.domain.model.message.Message;

public interface SaveMessagePort {
    Message save(Message message);
}