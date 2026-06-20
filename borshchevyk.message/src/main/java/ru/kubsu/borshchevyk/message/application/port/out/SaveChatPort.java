package ru.kubsu.borshchevyk.message.application.port.out;

import ru.kubsu.borshchevyk.message.domain.model.chat.Chat;

public interface SaveChatPort {
    Chat save(Chat chat);
}