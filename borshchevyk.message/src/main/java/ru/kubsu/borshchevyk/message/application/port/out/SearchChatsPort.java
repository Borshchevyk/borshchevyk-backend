package ru.kubsu.borshchevyk.message.application.port.out;

import ru.kubsu.borshchevyk.message.domain.model.chat.Chat;

import java.util.List;

public interface SearchChatsPort {
    List<Chat> searchChats(String query);
}