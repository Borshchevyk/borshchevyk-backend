package ru.kubsu.borshchevyk.message.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kubsu.borshchevyk.message.application.port.in.LoadUserChatsUseCase;
import ru.kubsu.borshchevyk.message.application.port.out.ChatMemberPort;
import ru.kubsu.borshchevyk.message.application.port.out.ChatPort;
import ru.kubsu.borshchevyk.message.domain.model.chat.Chat;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatMember;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * LoadUserChatsService implementation.
 *
 * @author Aleksey Timko
 * @since 2026-05-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoadUserChatsService implements LoadUserChatsUseCase {

    private final ChatPort chatPort;
    private final ChatMemberPort chatMemberPort;

    @Override
    @Transactional(readOnly = true)
    public List<Chat> loadUserChats(UserId userId) {
        log.info("Loading chats for user: {}", userId.value());
        List<ChatMember> members = chatMemberPort.findByUserId(userId);
        if (members.isEmpty()) {
            return new ArrayList<>();
        }
        List<ChatId> chatIds = members.stream()
                .map(ChatMember::getChatId)
                .collect(Collectors.toList());
        List<Chat> chats = chatPort.findByIdIn(chatIds);
        return chats.stream()
                .filter(chat -> !chat.isDeleted())
                .collect(Collectors.toList());
    }
}
