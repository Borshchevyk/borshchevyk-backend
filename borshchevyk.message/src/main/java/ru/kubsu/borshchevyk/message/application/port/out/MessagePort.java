package ru.kubsu.borshchevyk.message.application.port.out;

import ru.kubsu.borshchevyk.message.domain.model.message.Message;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.domain.model.value.MessageId;

import java.util.List;
import java.util.Optional;

public interface MessagePort {
    Message save(Message message);
    List<Message> loadChatHistory(ChatId chatId, ru.kubsu.borshchevyk.message.domain.model.value.UserId userId, java.time.LocalDateTime historyClearedAt, int page, int size);
    List<Message> loadMessageComments(ChatId chatId, MessageId parentMessageId, ru.kubsu.borshchevyk.message.domain.model.value.UserId userId, int page, int size);
    int countPinnedMessagesByChatId(ChatId chatId);
    List<Message> findPinnedMessagesByChatId(ChatId chatId);
    Optional<Message> findById(MessageId messageId);
    Optional<Message> getLastMessage(ChatId chatId, ru.kubsu.borshchevyk.message.domain.model.value.UserId userId, java.time.LocalDateTime historyClearedAt);
    long countUnreadMessages(ChatId chatId, ru.kubsu.borshchevyk.message.domain.model.value.UserId userId, java.time.LocalDateTime historyClearedAt, java.time.LocalDateTime lastReadAt);
}
