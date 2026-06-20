package ru.kubsu.borshchevyk.message.application.port.out;

import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;

public interface PublishChatEventPort {
    void publishChatEvent(UserId userId, ChatId chatId, String action);
}