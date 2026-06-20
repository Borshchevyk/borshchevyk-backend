package ru.kubsu.borshchevyk.message.application.port.out;

import ru.kubsu.borshchevyk.message.domain.model.chat.ChatMember;

import java.util.List;

public interface SaveChatMembersPort {
    void saveAll(List<ChatMember> members);
}