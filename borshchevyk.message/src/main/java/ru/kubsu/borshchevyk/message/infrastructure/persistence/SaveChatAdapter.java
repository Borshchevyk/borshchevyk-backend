package ru.kubsu.borshchevyk.message.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.message.application.port.out.SaveChatPort;
import ru.kubsu.borshchevyk.message.domain.model.chat.Chat;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.entity.ChatEntity;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.mapper.ChatMapper;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.repository.ChatRepository;

@Component
@RequiredArgsConstructor
public class SaveChatAdapter implements SaveChatPort {

    private final ChatRepository chatRepository;
    private final ChatMapper chatMapper;

    @Override
    public Chat save(Chat chat) {
        ChatEntity entity = chatMapper.toEntity(chat);
        ChatEntity saved = chatRepository.save(entity);
        return chatMapper.toDomain(saved);
    }
}
