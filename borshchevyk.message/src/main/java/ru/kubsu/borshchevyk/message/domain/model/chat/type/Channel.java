package ru.kubsu.borshchevyk.message.domain.model.chat.type;

import lombok.*;
import lombok.experimental.SuperBuilder;
import ru.kubsu.borshchevyk.message.domain.model.chat.Chat;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Channel extends Chat {

    private String title;
    private String description;
    private String inviteCode;

    @Builder.Default
    private boolean commentsEnabled = true;

    @Override
    public void updateInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    @Override
    public void validateInviteLinkGeneration() { }

    @Override
    public <T> T accept(ru.kubsu.borshchevyk.message.domain.model.chat.visitor.ChatVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public void updateInfo(String title, String description, Boolean commentsEnabled) {
        if (title != null && !title.isBlank()) {
            this.title = title;
        }
        if (description != null) {
            this.description = description;
        }
        if (commentsEnabled != null) {
            this.commentsEnabled = commentsEnabled;
        }
    }
}