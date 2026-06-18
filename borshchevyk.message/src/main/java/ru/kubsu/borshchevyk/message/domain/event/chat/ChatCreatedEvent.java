package ru.kubsu.borshchevyk.message.domain.event.chat;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import java.util.UUID;
import java.util.List;

@Getter
public class ChatCreatedEvent extends ApplicationEvent {
    private final UUID chatId;
    private final UUID creatorId;
    private final List<UUID> initialMemberIds;

    public ChatCreatedEvent(Object source, UUID chatId, UUID creatorId, List<UUID> initialMemberIds) {
        super(source);
        this.chatId = chatId;
        this.creatorId = creatorId;
        this.initialMemberIds = initialMemberIds;
    }
}