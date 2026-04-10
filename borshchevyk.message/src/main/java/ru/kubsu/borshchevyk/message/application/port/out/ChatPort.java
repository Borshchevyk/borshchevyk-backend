package ru.kubsu.borshchevyk.message.application.port.out;

import ru.kubsu.borshchevyk.message.domain.model.chat.Chat;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;

import java.util.List;
import java.util.Optional;

public interface ChatPort {
    Chat save(Chat chat);
    Optional<Chat> findById(ChatId chatId);
    List<Chat> findByIdIn(List<ChatId> chatIds);
}
