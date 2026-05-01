package ru.kubsu.borshchevyk.message.application.port.in;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatMember;

/**
 * UseCase for loading members of a chat with pagination.
 *
 * @author Aleksey Timko
 */
public interface LoadChatMembersUseCase {
    Page<ChatMember> loadChatMembers(UUID chatId, UUID requesterId, Pageable pageable);
}
