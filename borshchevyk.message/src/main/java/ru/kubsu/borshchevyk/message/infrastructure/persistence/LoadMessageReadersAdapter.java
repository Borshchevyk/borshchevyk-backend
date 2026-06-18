package ru.kubsu.borshchevyk.message.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.message.application.port.out.LoadMessageReadersPort;
import ru.kubsu.borshchevyk.message.domain.model.value.MessageId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.repository.MessageReaderRepository;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class LoadMessageReadersAdapter implements LoadMessageReadersPort {

    private final MessageReaderRepository messageReaderRepository;

    @Override
    public List<UserId> findReaders(MessageId messageId) {
        return messageReaderRepository.findByMessageId(messageId.value())
                .stream()
                .map(entity -> new UserId(entity.getUserId()))
                .collect(Collectors.toList());
    }
}
