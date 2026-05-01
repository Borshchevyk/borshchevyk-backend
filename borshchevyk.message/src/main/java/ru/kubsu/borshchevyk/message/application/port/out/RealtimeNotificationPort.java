package ru.kubsu.borshchevyk.message.application.port.out;

import ru.kubsu.borshchevyk.message.domain.model.message.Message;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;

/**
 * Output port for sending real-time notifications (e.g., via WebSocket).
 *
 * @author Aleksey Timko
 */
public interface RealtimeNotificationPort {
    void notifyUser(UserId userId, Message message);
    void notifyChatEvent(UserId userId, ChatId chatId, String action);
}
