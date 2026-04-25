package ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatRole;

public record ChatMemberResponse(
        UUID chatId,
        UUID userId,
        ShortUserDto userDetails,
        ChatRole role,
        LocalDateTime joinedAt,
        boolean canSendMessages,
        boolean canDeleteMessages,
        boolean canInviteUsers,
        boolean canChangeInfo,
        UUID lastReadMessageId
) {
}
