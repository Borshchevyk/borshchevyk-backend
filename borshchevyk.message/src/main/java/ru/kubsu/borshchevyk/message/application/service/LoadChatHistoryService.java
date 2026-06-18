package ru.kubsu.borshchevyk.message.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kubsu.borshchevyk.message.application.dto.query.LoadChatHistoryQuery;
import ru.kubsu.borshchevyk.message.application.port.in.LoadChatHistoryUseCase;
import ru.kubsu.borshchevyk.message.application.port.out.LoadChatHistoryPort;
import ru.kubsu.borshchevyk.message.application.port.out.LoadChatMemberPort;
import ru.kubsu.borshchevyk.message.application.port.out.LoadChatPort;
import ru.kubsu.borshchevyk.message.domain.exception.ChatNotFoundException;
import ru.kubsu.borshchevyk.message.domain.exception.UserNotInChatException;
import ru.kubsu.borshchevyk.message.domain.model.chat.Chat;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatMember;
import ru.kubsu.borshchevyk.message.domain.model.message.Message;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoadChatHistoryService implements LoadChatHistoryUseCase {

    private final LoadChatHistoryPort loadChatHistoryPort;
    private final LoadChatMemberPort loadChatMemberPort;
    private final LoadChatPort loadChatPort;

    @Override
    @Transactional(readOnly = true)
    public List<Message> loadChatHistory(LoadChatHistoryQuery query) {
        log.info("Loading chat history for chat: {}", query.chatId());

        ChatId chatId = new ChatId(query.chatId());
        UserId userId = new UserId(query.userId());

        Chat chat = loadChatPort.findById(chatId)
                .orElseThrow(() -> new ChatNotFoundException("Chat not found with id: " + query.chatId()));

        if (chat.isDeleted()) {
            throw new ChatNotFoundException("Chat not found or deleted with id: " + query.chatId());
        }

        ChatMember chatMember = loadChatMemberPort.findByChatIdAndUserId(chatId, userId)
                .orElseThrow(() -> new UserNotInChatException("User " + query.userId() + " is not a member of chat " + query.chatId()));

        return loadChatHistoryPort.loadChatHistory(chatId, userId, chatMember.getHistoryClearedAt(), query.page(), query.size());
    }
}