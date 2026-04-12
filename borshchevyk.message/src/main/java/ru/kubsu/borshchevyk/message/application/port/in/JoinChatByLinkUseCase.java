package ru.kubsu.borshchevyk.message.application.port.in;

import java.util.UUID;
import ru.kubsu.borshchevyk.message.domain.model.chat.Chat;

public interface JoinChatByLinkUseCase {
    Chat joinChatByLink(String inviteCode, UUID userId);
}
