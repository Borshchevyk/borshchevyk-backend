package ru.kubsu.borshchevyk.message.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kubsu.borshchevyk.message.application.port.in.SearchChatsUseCase;
import ru.kubsu.borshchevyk.message.application.port.out.ChatPort;
import ru.kubsu.borshchevyk.message.domain.model.chat.Chat;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchChatsService implements SearchChatsUseCase {

    private final ChatPort chatPort;

    @Override
    @Transactional(readOnly = true)
    public List<Chat> searchPublicChats(String query) {
        log.info("Searching public chats with query: {}", query);
        if (query == null || query.trim().isEmpty()) {
            return List.of();
        }
        return chatPort.searchPublicChats(query);
    }
}
