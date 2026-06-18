package ru.kubsu.borshchevyk.message.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.message.application.port.out.CheckMessageDeletedPort;
import ru.kubsu.borshchevyk.message.domain.model.value.MessageId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.repository.DeletedMessageRepository;

@Component
@RequiredArgsConstructor
public class CheckMessageDeletedAdapter implements CheckMessageDeletedPort {

    private final DeletedMessageRepository repository;

    @Override
    public boolean isDeletedForUser(MessageId messageId, UserId userId) {
        return repository.existsByMessageIdAndUserId(messageId.value(), userId.value());
    }
}
