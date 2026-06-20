package ru.kubsu.borshchevyk.message.application.port.out;

import ru.kubsu.borshchevyk.message.domain.model.chat.ChatMember;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;

import java.util.Optional;

public interface LoadChatMemberPort {
    Optional<ChatMember> findByChatIdAndUserId(ChatId chatId, UserId userId);
}