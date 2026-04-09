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
    public List<Message> findByChatId(ChatId chatId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return messageRepository.findByChatIdOrderByCreatedAtDesc(chatId.value(), pageable)
                .stream()
                .map(messageMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Message> findById(MessageId messageId) {
        return messageRepository.findById(messageId.value())
                .map(messageMapper::toDomain);
    }
}
