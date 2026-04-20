package ru.kubsu.borshchevyk.message.application.port.out;

import ru.kubsu.borshchevyk.message.domain.model.message.Message;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;

public interface RealtimeNotificationPort {
    void notifyUser(UserId userId, Message message);
    void notifyChatEvent(UserId userId, ru.kubsu.borshchevyk.message.domain.model.value.ChatId chatId, String action);
}
