package ru.kubsu.borshchevyk.message.application.port.in;

import java.util.UUID;

/**
 * UseCase for generating an invite link for a chat.
 *
 * @author Aleksey Timko
 */
public interface GenerateInviteLinkUseCase {
    String generateInviteLink(UUID chatId, UUID requesterId);
}
