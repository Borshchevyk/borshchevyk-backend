package ru.kubsu.borshchevyk.message.application.port.out;

import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;

public interface CountPinnedMessagesPort {
    int countPinnedMessagesByChatId(ChatId chatId);
}