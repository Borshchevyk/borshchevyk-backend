package ru.kubsu.borshchevyk.message.application.port.out;

import ru.kubsu.borshchevyk.message.domain.model.chat.ChatMember;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;

import java.util.List;
import java.util.Optional;

public interface ChatMemberPort {
    ChatMember save(ChatMember member);
    void saveAll(List<ChatMember> members);
    Optional<ChatMember> findByChatIdAndUserId(ChatId chatId, UserId userId);
    List<ChatMember> findByChatId(ChatId chatId);
    List<ChatMember> findByUserId(UserId userId);
    void delete(ChatMember member);
}
