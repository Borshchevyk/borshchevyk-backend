package ru.kubsu.borshchevyk.message.domain.event.chat;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import java.util.UUID;

@Getter
public class UserJoinedChatEvent extends ApplicationEvent {
    private final UUID chatId;
    private final UUID userId;
    private final UUID joinedUserId;

    public UserJoinedChatEvent(Object source, UUID chatId, UUID userId, UUID joinedUserId) {
        super(source);
        this.chatId = chatId;
        this.userId = userId;
        this.joinedUserId = joinedUserId;
    }
}