package ru.kubsu.borshchevyk.message.application.port.out;

import ru.kubsu.borshchevyk.message.domain.model.chat.Chat;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;

import java.util.Optional;

public interface LoadPrivateChatPort {
    Optional<Chat> findPrivateChatBetweenUsers(UserId userId1, UserId userId2);
}