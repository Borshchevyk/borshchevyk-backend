package ru.kubsu.borshchevyk.message.application.port.in;

import ru.kubsu.borshchevyk.message.domain.model.chat.Chat;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;

import java.util.List;

/**
 * UseCase for loading all chats a user is a member of.
 *
 * @author Aleksey Timko
 */
public interface LoadUserChatsUseCase {
    List<Chat> loadUserChats(UserId userId);
}
