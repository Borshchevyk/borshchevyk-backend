package ru.kubsu.borshchevyk.message.application.port.out;

import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;

import java.time.LocalDateTime;

public interface CountUnreadMessagesPort {
    long countUnreadMessages(ChatId chatId, UserId userId, LocalDateTime historyClearedAt, LocalDateTime lastReadAt);
}