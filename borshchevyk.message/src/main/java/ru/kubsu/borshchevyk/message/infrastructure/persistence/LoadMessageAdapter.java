package ru.kubsu.borshchevyk.message.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.message.application.port.out.LoadMessagePort;
import ru.kubsu.borshchevyk.message.domain.model.message.Message;
import ru.kubsu.borshchevyk.message.domain.model.value.MessageId;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.mapper.MessageMapper;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.repository.MessageRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class LoadMessageAdapter implements LoadMessagePort {

    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;

    @Override
    public Optional<Message> findById(MessageId messageId) {
        return messageRepository.findById(messageId.value())
                .map(messageMapper::toDomain);
    }
}
