package ru.kubsu.borshchevyk.message.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kubsu.borshchevyk.message.application.dto.query.LoadPinnedMessagesQuery;
import ru.kubsu.borshchevyk.message.application.port.in.LoadPinnedMessagesUseCase;
import ru.kubsu.borshchevyk.message.application.port.out.LoadChatMemberPort;
import ru.kubsu.borshchevyk.message.application.port.out.LoadPinnedMessagesPort;
import ru.kubsu.borshchevyk.message.domain.exception.UserNotInChatException;
import ru.kubsu.borshchevyk.message.domain.model.message.Message;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoadPinnedMessagesService implements LoadPinnedMessagesUseCase {

    private final LoadChatMemberPort loadChatMemberPort;
    private final LoadPinnedMessagesPort loadPinnedMessagesPort;

    @Override
    @Transactional(readOnly = true)
    public List<Message> loadPinnedMessages(LoadPinnedMessagesQuery query) {
        log.info("Loading pinned messages for chat {} by user {}", query.chatId(), query.requesterId());
        ChatId chatId = new ChatId(query.chatId());
        UserId requesterId = new UserId(query.requesterId());

        loadChatMemberPort.findByChatIdAndUserId(chatId, requesterId)
                .orElseThrow(() -> new UserNotInChatException("User is not a member of the chat"));

        return loadPinnedMessagesPort.findPinnedMessagesByChatId(chatId);
    }
}