package ru.kubsu.borshchevyk.message.application.port.out;

import ru.kubsu.borshchevyk.message.domain.model.message.Message;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.domain.model.value.MessageId;

import java.util.List;
import java.util.Optional;

public interface MessagePort {
    Message save(Message message);
    List<Message> findByChatId(ChatId chatId, int page, int size);
    List<Message> findCommentsByMessageId(ChatId chatId, MessageId parentMessageId, int page, int size);
    int countPinnedMessagesByChatId(ChatId chatId);
    List<Message> findPinnedMessagesByChatId(ChatId chatId);
    Optional<Message> findById(MessageId messageId);
}
