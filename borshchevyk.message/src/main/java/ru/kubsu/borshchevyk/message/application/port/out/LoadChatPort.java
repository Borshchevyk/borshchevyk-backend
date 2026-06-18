package ru.kubsu.borshchevyk.message.application.port.out;

import ru.kubsu.borshchevyk.message.domain.model.chat.Chat;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;

import java.util.Optional;

public interface LoadChatPort {
    Optional<Chat> findById(ChatId chatId);
}