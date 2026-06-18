package ru.kubsu.borshchevyk.message.application.port.out;

import ru.kubsu.borshchevyk.message.domain.model.message.Message;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;

import java.util.List;

public interface LoadPinnedMessagesPort {
    List<Message> findPinnedMessagesByChatId(ChatId chatId);
}