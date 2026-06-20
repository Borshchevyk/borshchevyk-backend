package ru.kubsu.borshchevyk.message.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kubsu.borshchevyk.message.application.dto.query.LoadMessageCommentsQuery;
import ru.kubsu.borshchevyk.message.application.port.in.LoadMessageCommentsUseCase;
import ru.kubsu.borshchevyk.message.application.port.out.LoadChatMemberPort;
import ru.kubsu.borshchevyk.message.application.port.out.LoadMessageCommentsPort;
import ru.kubsu.borshchevyk.message.application.port.out.LoadMessagePort;
import ru.kubsu.borshchevyk.message.domain.exception.MessageNotFoundException;
import ru.kubsu.borshchevyk.message.domain.exception.UserNotInChatException;
import ru.kubsu.borshchevyk.message.domain.model.message.Message;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.domain.model.value.MessageId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoadMessageCommentsService implements LoadMessageCommentsUseCase {

    private final LoadMessagePort loadMessagePort;
    private final LoadChatMemberPort loadChatMemberPort;
    private final LoadMessageCommentsPort loadMessageCommentsPort;

    @Override
    @Transactional(readOnly = true)
    public List<Message> loadMessageComments(LoadMessageCommentsQuery query) {
        log.info("Loading comments for message {} in chat {} by user {}", query.parentMessageId(), query.chatId(), query.requesterId());
        ChatId chatId = new ChatId(query.chatId());
        MessageId parentMessageId = new MessageId(query.parentMessageId());
        UserId requesterId = new UserId(query.requesterId());

        loadChatMemberPort.findByChatIdAndUserId(chatId, requesterId)
                .orElseThrow(() -> new UserNotInChatException("User is not a member of the chat"));

        Message message = loadMessagePort.findById(parentMessageId)
                .orElseThrow(() -> new MessageNotFoundException("Parent message not found"));

        if (!message.getChatId().equals(chatId)) {
            throw new IllegalArgumentException("Message does not belong to this chat");
        }

        return loadMessageCommentsPort.loadMessageComments(chatId, parentMessageId, requesterId, query.page(), query.size());
    }
}