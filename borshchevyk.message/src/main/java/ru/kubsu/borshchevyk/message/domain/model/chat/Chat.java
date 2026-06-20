package ru.kubsu.borshchevyk.message.domain.model.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ru.kubsu.borshchevyk.message.domain.exception.ForbiddenActionException;
import ru.kubsu.borshchevyk.message.domain.model.chat.member_permissions.ChatMemberPermissions;
import ru.kubsu.borshchevyk.message.domain.model.chat.type.Channel;
import ru.kubsu.borshchevyk.message.domain.model.reaction.Reaction;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;

import java.time.LocalDateTime;
import java.util.Set;

import ru.kubsu.borshchevyk.message.domain.model.chat.visitor.ChatVisitor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class Chat {

    private ChatId id;
    private ChatType type;
    private LocalDateTime createdAt;
    private boolean isDeleted;

    @Builder.Default
    private boolean isDeletable = true;

    @Builder.Default
    private Set<String> allowedReactions = Reaction.getAllReactions();

    public abstract <T> T accept(ChatVisitor<T> visitor);

    public void updateInfo(String title, String description, Boolean commentsEnabled) {
        throw new ForbiddenActionException("Cannot update info of this chat type");
    }

    public boolean canMemberSendMessage(ChatMember member, boolean isComment) {
        boolean hasSendMessagesPermissions = member.getPermissions().hasPermission(ChatMemberPermissions.PermissionType.SEND_MESSAGES);

        if (member.getRole() == ChatRole.OWNER) return true;
        if (this.type == ChatType.CHANNEL) {
            if (isComment) {
                return ((Channel) this).isCommentsEnabled() && hasSendMessagesPermissions;
            }
            return member.getRole() == ChatRole.ADMIN;
        }
        return member.getRole() == ChatRole.ADMIN || hasSendMessagesPermissions;
    }

    public boolean canMemberDeleteMessage(ChatMember member, boolean isAuthor) {
        boolean hasDeleteMessagesPermissions = member.getPermissions().hasPermission(ChatMemberPermissions.PermissionType.DELETE_MESSAGES);

        if (member.getRole() == ChatRole.OWNER) return true;
        if (isAuthor) {
            return hasDeleteMessagesPermissions;
        }
        return member.getRole() == ChatRole.ADMIN && hasDeleteMessagesPermissions;
    }

    public boolean canMemberPinMessage(ChatMember member) {
        if (member.getRole() == ChatRole.OWNER) return false;
        return member.getRole() != ChatRole.ADMIN && !member.getPermissions().hasPermission(ChatMemberPermissions.PermissionType.CHANGE_CHAT_INFO);
    }

    public void updateInviteCode(String inviteCode) {
        throw new ForbiddenActionException(
                "Invite links are not supported for chat type " + getType()
        );
    }

    public void validateInviteLinkGeneration() {
        throw new ForbiddenActionException(
                "Invite links are not supported for this chat type"
        );
    }

    public void validateCanLeaveChat() {
        if (this.type == ChatType.PRIVATE || this.type == ChatType.SAVED_MESSAGES) {
            throw new ForbiddenActionException("Cannot leave private or saved messages chats");
        }
    }
}