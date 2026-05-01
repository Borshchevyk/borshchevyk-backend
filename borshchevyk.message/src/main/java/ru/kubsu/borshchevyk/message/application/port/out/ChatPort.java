package ru.kubsu.borshchevyk.message.application.port.out;

import ru.kubsu.borshchevyk.message.domain.model.chat.Chat;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;

import java.util.List;
import java.util.Optional;

/**
 * Output port for managing chats in the persistence layer.
 *
 * @author Aleksey Timko
 */
public interface ChatPort {
    Chat save(Chat chat);
    Optional<Chat> findById(ChatId chatId);
    List<Chat> findByIdIn(List<ChatId> chatIds);
    Optional<Chat> findPrivateChatBetweenUsers(UserId userId1, UserId userId2);
    Optional<Chat> findByInviteCode(String inviteCode);
    List<Chat> searchPublicChats(String query);
}
