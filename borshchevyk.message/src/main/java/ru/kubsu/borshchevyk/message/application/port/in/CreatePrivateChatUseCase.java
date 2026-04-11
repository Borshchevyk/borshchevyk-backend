package ru.kubsu.borshchevyk.message.application.port.in;

import ru.kubsu.borshchevyk.message.domain.model.chat.Chat;
import java.util.UUID;

public interface CreatePrivateChatUseCase {
    Chat createPrivateChat(UUID requesterId, UUID targetUserId);
}
