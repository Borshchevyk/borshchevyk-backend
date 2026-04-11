package ru.kubsu.borshchevyk.message.application.port.in;

import java.util.UUID;

public interface GenerateInviteLinkUseCase {
    String generateInviteLink(UUID chatId, UUID requesterId);
}
