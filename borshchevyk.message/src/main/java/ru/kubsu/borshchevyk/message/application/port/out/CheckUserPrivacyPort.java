package ru.kubsu.borshchevyk.message.application.port.out;

import java.util.UUID;

/**
 * Port for checking user privacy settings.
 *
 * @author Aleksey Timko
 */
public interface CheckUserPrivacyPort {
    boolean canInviteToChat(UUID targetUserId, UUID requesterId);
}
