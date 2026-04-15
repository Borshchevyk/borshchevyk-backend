package ru.kubsu.borshchevyk.message.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.message.application.port.out.MessageReaderPort;
import ru.kubsu.borshchevyk.message.domain.model.value.MessageId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.entity.MessageReaderEntity;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.repository.MessageReaderRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MessageReaderAdapter implements MessageReaderPort {

    private final MessageReaderRepository messageReaderRepository;

    @Override
    public void save(MessageId messageId, UserId userId) {
        MessageReaderEntity entity = MessageReaderEntity.builder()
                .messageId(messageId.value())
                .userId(userId.value())
                .readAt(LocalDateTime.now())
                .build();
        messageReaderRepository.save(entity);
    }

    @Override
    public List<UserId> findReaders(MessageId messageId) {
        return messageReaderRepository.findByMessageId(messageId.value())
                .stream()
                .map(entity -> new UserId(entity.getUserId()))
                .collect(Collectors.toList());
    }
}
