package ru.kubsu.borshchevyk.message.application.port.out;

import ru.kubsu.borshchevyk.message.domain.model.chat.ChatMember;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;

import java.util.List;

public interface LoadChatMembersPort {
    List<ChatMember> findByChatId(ChatId chatId);
}