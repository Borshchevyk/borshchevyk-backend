package ru.kubsu.borshchevyk.message.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kubsu.borshchevyk.message.application.dto.query.LoadUserChatsQuery;
import ru.kubsu.borshchevyk.message.application.port.in.LoadUserChatsUseCase;
import ru.kubsu.borshchevyk.message.application.port.out.LoadChatsPort;
import ru.kubsu.borshchevyk.message.application.port.out.LoadUserChatsMembersPort;
import ru.kubsu.borshchevyk.message.domain.model.chat.Chat;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatMember;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoadUserChatsService implements LoadUserChatsUseCase {

    private final LoadChatsPort loadChatsPort;
    private final LoadUserChatsMembersPort loadUserChatsMembersPort;

    @Override
    @Transactional(readOnly = true)
    public List<Chat> loadUserChats(LoadUserChatsQuery query) {
        UserId userId = query.userId();
        log.info("Loading chats for user: {}", userId.value());
        List<ChatMember> members = loadUserChatsMembersPort.findByUserId(userId);
        if (members.isEmpty()) {
            return new ArrayList<>();
        }
        List<ChatId> chatIds = members.stream()
                .map(ChatMember::getChatId)
                .collect(Collectors.toList());
        List<Chat> chats = loadChatsPort.findByIdIn(chatIds);
        return chats.stream()
                .filter(chat -> !chat.isDeleted())
                .collect(Collectors.toList());
    }
}