package ru.kubsu.borshchevyk.message.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.message.application.port.out.CountPinnedMessagesPort;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.repository.MessageRepository;

@Component
@RequiredArgsConstructor
public class CountPinnedMessagesAdapter implements CountPinnedMessagesPort {

    private final MessageRepository messageRepository;

    @Override
    public int countPinnedMessagesByChatId(ChatId chatId) {
        return messageRepository.countByChatIdAndPinnedAtIsNotNull(chatId.value());
    }
}
