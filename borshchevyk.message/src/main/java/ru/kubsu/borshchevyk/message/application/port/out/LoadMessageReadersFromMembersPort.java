package ru.kubsu.borshchevyk.message.application.port.out;

import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;

import java.time.LocalDateTime;
import java.util.List;

public interface LoadMessageReadersFromMembersPort {
    List<UserId> findReadersOfMessage(ChatId chatId, LocalDateTime messageCreatedAt);
}