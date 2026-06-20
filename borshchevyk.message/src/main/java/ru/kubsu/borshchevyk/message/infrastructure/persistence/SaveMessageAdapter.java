package ru.kubsu.borshchevyk.message.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.message.application.port.out.SaveMessagePort;
import ru.kubsu.borshchevyk.message.domain.model.message.Message;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.entity.MessageEntity;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.mapper.MessageMapper;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.repository.MessageRepository;

@Component
@RequiredArgsConstructor
public class SaveMessageAdapter implements SaveMessagePort {

    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;

    @Override
    public Message save(Message message) {
        MessageEntity entity = messageMapper.toEntity(message);
        MessageEntity saved = messageRepository.save(entity);
        return messageMapper.toDomain(saved);
    }
}
