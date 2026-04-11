package ru.kubsu.borshchevyk.message.application.port.in;

import java.util.List;
import java.util.UUID;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatMember;

public interface LoadChatMembersUseCase {
    List<ChatMember> loadChatMembers(UUID chatId, UUID requesterId);
}
