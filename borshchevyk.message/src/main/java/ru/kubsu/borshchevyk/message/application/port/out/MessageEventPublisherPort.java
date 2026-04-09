package ru.kubsu.borshchevyk.message.application.port.out;

import ru.kubsu.borshchevyk.message.domain.model.message.Message;

public interface MessageEventPublisherPort {
    void publishMessageCreatedEvent(Message message);
    void publishMessageDeletedEvent(Message message);
}
