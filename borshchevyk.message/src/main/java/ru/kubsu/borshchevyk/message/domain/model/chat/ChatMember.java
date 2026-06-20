package ru.kubsu.borshchevyk.message.domain.model.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.kubsu.borshchevyk.message.domain.exception.ForbiddenActionException;
import ru.kubsu.borshchevyk.message.domain.model.chat.member_permissions.ChatMemberPermissions;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.domain.model.value.MessageId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMember {

    private ChatId chatId;
    private UserId userId;
    private ChatRole role;
    private LocalDateTime joinedAt;

    @Builder.Default
    private ChatMemberPermissions permissions = new ChatMemberPermissions();

    private LocalDateTime historyClearedAt;
    private MessageId lastReadMessageId;
    private LocalDateTime lastReadAt;

    @Builder.Default
    private boolean isPinned = false;

    public void validateCanGenerateInviteLink() {
        if (role == ChatRole.OWNER) {
            return;
        }

        if (permissions.hasPermission(ChatMemberPermissions.PermissionType.INVITE_USERS)) {
            return;
        }

        throw new ForbiddenActionException(
                "User does not have permission to generate invite links"
        );
    }

    public void validateCanKickUsers(ChatMember target) {
        if (this.role == ChatRole.MEMBER) {
            throw new ForbiddenActionException("MEMBER cannot kick users");
        }

        if (this.role == ChatRole.ADMIN && (target.getRole() == ChatRole.ADMIN || target.getRole() == ChatRole.OWNER)) {
            throw new ForbiddenActionException("ADMIN cannot kick other ADMINs or OWNERs");
        }
    }

    public void validateCanDeleteChat(Chat chat) {
        if (chat.getType() != ChatType.PRIVATE && this.role != ChatRole.OWNER) {
            throw new ForbiddenActionException("Only OWNER can delete group or channel chats");
        }
    }
}