package ru.kubsu.borshchevyk.message.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.message.application.port.out.LoadChatPort;
import ru.kubsu.borshchevyk.message.domain.model.chat.Chat;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.mapper.ChatMapper;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.repository.ChatRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class LoadChatAdapter implements LoadChatPort {

    private final ChatRepository chatRepository;
    private final ChatMapper chatMapper;

    @Override
    public Optional<Chat> findById(ChatId chatId) {
        return chatRepository.findById(chatId.value())
                .map(chatMapper::toDomain);
    }
}
