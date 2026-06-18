package ru.kubsu.borshchevyk.message.application.port.out;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatMember;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;

public interface LoadChatMembersPagePort {
    Page<ChatMember> findByChatId(ChatId chatId, Pageable pageable);
}