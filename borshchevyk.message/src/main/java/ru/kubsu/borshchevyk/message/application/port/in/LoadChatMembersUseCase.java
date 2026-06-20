package ru.kubsu.borshchevyk.message.application.port.in;

import org.springframework.data.domain.Page;
import ru.kubsu.borshchevyk.message.application.dto.query.LoadChatMembersQuery;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatMember;

public interface LoadChatMembersUseCase {
    Page<ChatMember> loadChatMembers(LoadChatMembersQuery query);
}