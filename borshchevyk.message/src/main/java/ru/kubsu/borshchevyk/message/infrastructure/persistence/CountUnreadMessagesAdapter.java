package ru.kubsu.borshchevyk.message.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.message.application.port.out.CountUnreadMessagesPort;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.repository.MessageRepository;

@Component
@RequiredArgsConstructor
public class CountUnreadMessagesAdapter implements CountUnreadMessagesPort {

    private final MessageRepository messageRepository;

    @Override
    public long countUnreadMessages(ChatId chatId, ru.kubsu.borshchevyk.message.domain.model.value.UserId userId, java.time.LocalDateTime historyClearedAt, java.time.LocalDateTime lastReadAt) {
        return messageRepository.countUnreadMessages(chatId.value(), userId.value(), historyClearedAt, lastReadAt);
    }
}
