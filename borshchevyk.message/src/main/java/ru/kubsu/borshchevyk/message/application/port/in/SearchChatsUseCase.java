package ru.kubsu.borshchevyk.message.application.port.in;

import ru.kubsu.borshchevyk.message.domain.model.chat.Chat;

import java.util.List;

public interface SearchChatsUseCase {
    List<Chat> searchPublicChats(String query);
}
