package ru.kubsu.borshchevyk.message.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.message.application.port.out.SaveDeletedMessagePort;
import ru.kubsu.borshchevyk.message.domain.model.value.MessageId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.entity.DeletedMessageEntity;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.repository.DeletedMessageRepository;

@Component
@RequiredArgsConstructor
public class SaveDeletedMessageAdapter implements SaveDeletedMessagePort {

    private final DeletedMessageRepository repository;

    @Override
    public void save(MessageId messageId, UserId userId) {
        repository.save(DeletedMessageEntity.builder()
                .messageId(messageId.value())
                .userId(userId.value())
                .build());
    }
}
