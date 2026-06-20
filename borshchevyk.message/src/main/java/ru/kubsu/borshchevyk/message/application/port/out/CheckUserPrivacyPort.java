package ru.kubsu.borshchevyk.message.application.port.out;

import java.util.UUID;

public interface CheckUserPrivacyPort {
    boolean canInviteToChat(UUID targetUserId, UUID requesterId);
}