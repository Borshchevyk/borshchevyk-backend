package ru.kubsu.borshchevyk.message.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.message.application.port.out.LoadPrivateChatPort;
import ru.kubsu.borshchevyk.message.domain.model.chat.Chat;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.mapper.ChatMapper;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.repository.ChatRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class LoadPrivateChatAdapter implements LoadPrivateChatPort {

    private final ChatRepository chatRepository;
    private final ChatMapper chatMapper;

    @Override
    public Optional<Chat> findPrivateChatBetweenUsers(UserId userId1, UserId userId2) {
        return chatRepository.findPrivateChatBetweenUsers(userId1.value(), userId2.value())
                .map(chatMapper::toDomain);
    }
}
