package ru.kubsu.borshchevyk.message.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.message.application.port.out.LoadChatsPort;
import ru.kubsu.borshchevyk.message.domain.model.chat.Chat;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.mapper.ChatMapper;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.repository.ChatRepository;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class LoadChatsAdapter implements LoadChatsPort {

    private final ChatRepository chatRepository;
    private final ChatMapper chatMapper;

    @Override
    public List<Chat> findByIdIn(List<ChatId> chatIds) {
        List<java.util.UUID> uuids = chatIds.stream()
                .map(ChatId::value)
                .collect(Collectors.toList());
        return chatRepository.findByIdIn(uuids)
                .stream()
                .map(chatMapper::toDomain)
                .collect(Collectors.toList());
    }
}
