package ru.kubsu.borshchevyk.message.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kubsu.borshchevyk.message.application.dto.query.LoadChatMembersQuery;
import ru.kubsu.borshchevyk.message.application.port.in.LoadChatMembersUseCase;
import ru.kubsu.borshchevyk.message.application.port.out.LoadChatMemberPort;
import ru.kubsu.borshchevyk.message.application.port.out.LoadChatMembersPagePort;
import ru.kubsu.borshchevyk.message.application.port.out.LoadChatPort;
import ru.kubsu.borshchevyk.message.domain.exception.ChatNotFoundException;
import ru.kubsu.borshchevyk.message.domain.exception.UserNotInChatException;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatMember;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoadChatMembersService implements LoadChatMembersUseCase {

    private final LoadChatMemberPort loadChatMemberPort;
    private final LoadChatMembersPagePort loadChatMembersPagePort;
    private final LoadChatPort loadChatPort;

    @Override
    @Transactional(readOnly = true)
    public Page<ChatMember> loadChatMembers(LoadChatMembersQuery query) {
        log.info("Loading members for chat {} by user {}", query.chatId(), query.requesterId());
        ChatId chatId = new ChatId(query.chatId());
        UserId requesterId = new UserId(query.requesterId());

        loadChatPort.findById(chatId)
                .orElseThrow(() -> new ChatNotFoundException("Chat not found"));

        loadChatMemberPort.findByChatIdAndUserId(chatId, requesterId)
                .orElseThrow(() -> new UserNotInChatException("Requester is not in the chat"));

        return loadChatMembersPagePort.findByChatId(chatId, query.pageable());
    }
}