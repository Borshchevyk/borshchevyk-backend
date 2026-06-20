package ru.kubsu.borshchevyk.message.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.message.application.port.out.SearchChatsPort;
import ru.kubsu.borshchevyk.message.domain.model.chat.Chat;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.mapper.ChatMapper;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.repository.ChatRepository;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SearchChatsAdapter implements SearchChatsPort {

    private final ChatRepository chatRepository;
    private final ChatMapper chatMapper;

    @Override
    public List<Chat> searchChats(String query) {
        return chatRepository.searchPublicChats(query).stream()
                .map(chatMapper::toDomain)
                .collect(Collectors.toList());
    }
}
