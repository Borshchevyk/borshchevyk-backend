package ru.kubsu.borshchevyk.message.domain.model.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Abstract domain aggregate root representing a Chat.
 *
 * @author Aleksey Timko
 */
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
    private Set<String> allowedReactions = new HashSet<>(Set.of("👍", "👎", "❤️", "🔥", "😂", "😢"));

    public boolean canMemberSendMessage(ChatMember member, boolean isComment) {
        if (member.getRole() == ChatRole.OWNER) return true;
        if (this.type == ChatType.CHANNEL) {
            if (isComment) {
                return ((Channel) this).isCommentsEnabled() && member.isCanSendMessages();
            }
            return member.getRole() == ChatRole.ADMIN;
        }
        return member.getRole() == ChatRole.ADMIN || member.isCanSendMessages();
    }

    public boolean canMemberDeleteMessage(ChatMember member, boolean isAuthor) {
        if (member.getRole() == ChatRole.OWNER) return true;
        if (isAuthor) {
            return member.isCanDeleteMessages();
        }
        return member.getRole() == ChatRole.ADMIN && member.isCanDeleteMessages();
    }

    public boolean canMemberPinMessage(ChatMember member) {
        if (member.getRole() == ChatRole.OWNER) return true;
        return member.getRole() == ChatRole.ADMIN || member.isCanChangeInfo();
    }
}
