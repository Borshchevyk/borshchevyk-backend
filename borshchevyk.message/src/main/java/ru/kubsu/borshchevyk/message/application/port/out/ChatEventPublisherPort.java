package ru.kubsu.borshchevyk.message.application.port.out;

import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;

/**
 * Output port for publishing chat events to external systems (e.g., Kafka).
 *
 * @author Aleksey Timko
 */
public interface ChatEventPublisherPort {
    void publishChatEvent(UserId userId, ChatId chatId, String action);
}
