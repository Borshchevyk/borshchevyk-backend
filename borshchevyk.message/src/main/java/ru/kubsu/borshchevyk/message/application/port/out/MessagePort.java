package ru.kubsu.borshchevyk.message.application.port.out;

import ru.kubsu.borshchevyk.message.domain.model.message.Message;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.domain.model.value.MessageId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Output port for managing messages in the persistence layer.
 *
 * @author Aleksey Timko
 */
public interface MessagePort {
    Message save(Message message);
    List<Message> loadChatHistory(ChatId chatId, UserId userId, LocalDateTime historyClearedAt, int page, int size);
    List<Message> loadMessageComments(ChatId chatId, MessageId parentMessageId, UserId userId, int page, int size);
    int countPinnedMessagesByChatId(ChatId chatId);
    List<Message> findPinnedMessagesByChatId(ChatId chatId);
    Optional<Message> findById(MessageId messageId);
    Optional<Message> getLastMessage(ChatId chatId, UserId userId, LocalDateTime historyClearedAt);
    long countUnreadMessages(ChatId chatId, UserId userId, LocalDateTime historyClearedAt, LocalDateTime lastReadAt);
}
