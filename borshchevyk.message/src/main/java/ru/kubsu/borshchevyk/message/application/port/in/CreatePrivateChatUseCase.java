package ru.kubsu.borshchevyk.message.application.port.in;

import ru.kubsu.borshchevyk.message.domain.model.chat.Chat;
import java.util.UUID;

/**
 * UseCase for creating a private chat between two users.
 *
 * @author Aleksey Timko
 */
public interface CreatePrivateChatUseCase {
    Chat createPrivateChat(UUID requesterId, UUID targetUserId);
}
