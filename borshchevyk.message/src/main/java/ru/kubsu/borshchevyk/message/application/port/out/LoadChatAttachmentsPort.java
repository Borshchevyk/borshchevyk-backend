package ru.kubsu.borshchevyk.message.application.port.out;

import ru.kubsu.borshchevyk.message.domain.model.message.Message;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;

import java.time.LocalDateTime;
import java.util.List;

public interface LoadChatAttachmentsPort {
    List<Message> loadChatAttachments(ChatId chatId, UserId userId, String type, LocalDateTime historyClearedAt, int page, int size);
}