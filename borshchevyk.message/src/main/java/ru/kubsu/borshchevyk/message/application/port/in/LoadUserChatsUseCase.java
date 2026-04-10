package ru.kubsu.borshchevyk.message.application.port.in;

import ru.kubsu.borshchevyk.message.domain.model.chat.Chat;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;

import java.util.List;

public interface LoadUserChatsUseCase {
    List<Chat> loadUserChats(UserId userId);
}
