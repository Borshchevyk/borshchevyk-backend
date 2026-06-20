package ru.kubsu.borshchevyk.message.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.message.application.port.out.LoadLastMessagePort;
import ru.kubsu.borshchevyk.message.domain.model.message.Message;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.mapper.MessageMapper;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.repository.MessageRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class LoadLastMessageAdapter implements LoadLastMessagePort {

    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;

    @Override
    public Optional<Message> getLastMessage(ChatId chatId, ru.kubsu.borshchevyk.message.domain.model.value.UserId userId, java.time.LocalDateTime historyClearedAt) {
        Pageable pageable = PageRequest.of(0, 1);
        return messageRepository.loadChatHistory(chatId.value(), userId.value(), historyClearedAt, pageable)
                .stream()
                .map(messageMapper::toDomain)
                .findFirst();
    }
}
