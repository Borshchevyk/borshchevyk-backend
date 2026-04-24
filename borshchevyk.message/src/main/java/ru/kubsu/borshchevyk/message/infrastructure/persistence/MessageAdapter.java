package ru.kubsu.borshchevyk.message.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.message.application.port.out.MessagePort;
import ru.kubsu.borshchevyk.message.domain.model.message.Message;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.domain.model.value.MessageId;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.entity.MessageEntity;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.mapper.MessageMapper;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.repository.MessageRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MessageAdapter implements MessagePort {

    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;

    @Override
    public Message save(Message message) {
        MessageEntity entity = messageMapper.toEntity(message);
        MessageEntity saved = messageRepository.save(entity);
        return messageMapper.toDomain(saved);
    }

    @Override
    public List<Message> loadChatHistory(ChatId chatId, ru.kubsu.borshchevyk.message.domain.model.value.UserId userId, java.time.LocalDateTime historyClearedAt, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return messageRepository.loadChatHistory(chatId.value(), userId.value(), historyClearedAt, pageable)
                .stream()
                .map(messageMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Message> loadMessageComments(ChatId chatId, MessageId parentMessageId, ru.kubsu.borshchevyk.message.domain.model.value.UserId userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return messageRepository.loadMessageComments(chatId.value(), parentMessageId.value(), userId.value(), pageable)
                .stream()
                .map(messageMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public int countPinnedMessagesByChatId(ChatId chatId) {
        return messageRepository.countByChatIdAndPinnedAtIsNotNull(chatId.value());
    }

    @Override
    public List<Message> findPinnedMessagesByChatId(ChatId chatId) {
        return messageRepository.findByChatIdAndPinnedAtIsNotNullOrderByPinnedAtDesc(chatId.value())
                .stream()
                .map(messageMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Message> findById(MessageId messageId) {
        return messageRepository.findById(messageId.value())
                .map(messageMapper::toDomain);
    }

    @Override
    public Optional<Message> getLastMessage(ChatId chatId, ru.kubsu.borshchevyk.message.domain.model.value.UserId userId, java.time.LocalDateTime historyClearedAt) {
        Pageable pageable = PageRequest.of(0, 1);
        return messageRepository.loadChatHistory(chatId.value(), userId.value(), historyClearedAt, pageable)
                .stream()
                .map(messageMapper::toDomain)
                .findFirst();
    }

    @Override
    public long countUnreadMessages(ChatId chatId, ru.kubsu.borshchevyk.message.domain.model.value.UserId userId, java.time.LocalDateTime historyClearedAt, java.time.LocalDateTime lastReadAt) {
        return messageRepository.countUnreadMessages(chatId.value(), userId.value(), historyClearedAt, lastReadAt);
    }
}
