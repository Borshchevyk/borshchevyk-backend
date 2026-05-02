package ru.kubsu.borshchevyk.message.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kubsu.borshchevyk.message.application.port.in.LoadChatAttachmentsUseCase;
import ru.kubsu.borshchevyk.message.application.port.out.ChatMemberPort;
import ru.kubsu.borshchevyk.message.application.port.out.MessagePort;
import ru.kubsu.borshchevyk.message.domain.exception.UserNotInChatException;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatMember;
import ru.kubsu.borshchevyk.message.domain.model.message.Message;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;

import java.util.List;
import java.util.UUID;

/**
 * Implementation of LoadChatAttachmentsUseCase.
 *
 * @author Aleksey Timko
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoadChatAttachmentsService implements LoadChatAttachmentsUseCase {

    private final MessagePort messagePort;
    private final ChatMemberPort chatMemberPort;

    @Override
    @Transactional(readOnly = true)
    public List<Message> loadChatAttachments(UUID chatId, UUID userId, String type, int page, int size) {
        ChatId cId = new ChatId(chatId);
        UserId uId = new UserId(userId);

        ChatMember member = chatMemberPort.findByChatIdAndUserId(cId, uId)
                .orElseThrow(() -> new UserNotInChatException("User is not a member of the chat"));

        return messagePort.loadChatAttachments(cId, uId, type, member.getHistoryClearedAt(), page, size);
    }
}