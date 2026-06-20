package ru.kubsu.borshchevyk.message.application.port.out;

import ru.kubsu.borshchevyk.message.domain.model.message.Message;
import ru.kubsu.borshchevyk.message.domain.model.value.MessageId;

import java.util.Optional;

public interface LoadMessagePort {
    Optional<Message> findById(MessageId messageId);
}