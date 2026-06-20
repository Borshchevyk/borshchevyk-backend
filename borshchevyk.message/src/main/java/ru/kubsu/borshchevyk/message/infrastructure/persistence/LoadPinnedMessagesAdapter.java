package ru.kubsu.borshchevyk.message.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.message.application.port.out.LoadPinnedMessagesPort;
import ru.kubsu.borshchevyk.message.domain.model.message.Message;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.mapper.MessageMapper;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.repository.MessageRepository;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class LoadPinnedMessagesAdapter implements LoadPinnedMessagesPort {

    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;

    @Override
    public List<Message> findPinnedMessagesByChatId(ChatId chatId) {
        return messageRepository.findByChatIdAndPinnedAtIsNotNullOrderByPinnedAtDesc(chatId.value())
                .stream()
                .map(messageMapper::toDomain)
                .collect(Collectors.toList());
    }
}
