package ru.kubsu.borshchevyk.message.application.port.in;

import java.util.UUID;
import ru.kubsu.borshchevyk.message.domain.model.chat.Chat;

/**
 * UseCase for joining a chat via an invite link.
 *
 * @author Aleksey Timko
 */
public interface JoinChatByLinkUseCase {
    Chat joinChatByLink(String inviteCode, UUID userId);
}
