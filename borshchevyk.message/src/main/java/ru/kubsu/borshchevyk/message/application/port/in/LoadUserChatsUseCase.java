package ru.kubsu.borshchevyk.message.application.port.in;

import ru.kubsu.borshchevyk.message.application.dto.query.LoadUserChatsQuery;
import ru.kubsu.borshchevyk.message.domain.model.chat.Chat;

import java.util.List;

public interface LoadUserChatsUseCase {
    List<Chat> loadUserChats(LoadUserChatsQuery query);
}