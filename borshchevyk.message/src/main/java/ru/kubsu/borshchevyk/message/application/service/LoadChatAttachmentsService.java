package ru.kubsu.borshchevyk.message.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kubsu.borshchevyk.message.application.dto.query.LoadChatAttachmentsQuery;
import ru.kubsu.borshchevyk.message.application.port.in.LoadChatAttachmentsUseCase;
import ru.kubsu.borshchevyk.message.application.port.out.LoadChatAttachmentsPort;
import ru.kubsu.borshchevyk.message.application.port.out.LoadChatMemberPort;
import ru.kubsu.borshchevyk.message.domain.exception.UserNotInChatException;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatMember;
import ru.kubsu.borshchevyk.message.domain.model.message.Message;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoadChatAttachmentsService implements LoadChatAttachmentsUseCase {

    private final LoadChatMemberPort loadChatMemberPort;
    private final LoadChatAttachmentsPort loadChatAttachmentsPort;

    @Override
    @Transactional(readOnly = true)
    public List<Message> loadChatAttachments(LoadChatAttachmentsQuery query) {
        ChatId cId = new ChatId(query.chatId());
        UserId uId = new UserId(query.userId());

        ChatMember member = loadChatMemberPort.findByChatIdAndUserId(cId, uId)
                .orElseThrow(() -> new UserNotInChatException("User is not a member of the chat"));

        return loadChatAttachmentsPort.loadChatAttachments(cId, uId, query.type(), member.getHistoryClearedAt(), query.page(), query.size());
    }
}