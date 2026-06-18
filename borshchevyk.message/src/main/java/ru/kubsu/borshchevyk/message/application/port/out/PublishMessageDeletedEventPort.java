package ru.kubsu.borshchevyk.message.application.port.out;

import ru.kubsu.borshchevyk.message.domain.model.message.Message;

import java.util.List;

public interface PublishMessageDeletedEventPort {
    void publishMessageDeletedEvent(Message message, List<String> targetUserIds);
}