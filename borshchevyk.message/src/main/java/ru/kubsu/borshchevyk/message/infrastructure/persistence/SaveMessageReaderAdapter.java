package ru.kubsu.borshchevyk.message.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.message.application.port.out.SaveMessageReaderPort;
import ru.kubsu.borshchevyk.message.domain.model.value.MessageId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.entity.MessageReaderEntity;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.repository.MessageReaderRepository;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class SaveMessageReaderAdapter implements SaveMessageReaderPort {

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
}
