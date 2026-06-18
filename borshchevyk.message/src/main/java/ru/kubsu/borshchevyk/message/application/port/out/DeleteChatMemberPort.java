package ru.kubsu.borshchevyk.message.application.port.out;

import ru.kubsu.borshchevyk.message.domain.model.chat.ChatMember;

public interface DeleteChatMemberPort {
    void delete(ChatMember member);
}