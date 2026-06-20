package ru.kubsu.borshchevyk.message.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kubsu.borshchevyk.message.application.dto.command.ClearChatHistoryCommand;
import ru.kubsu.borshchevyk.message.application.port.in.ClearChatHistoryUseCase;
import ru.kubsu.borshchevyk.message.application.port.out.*;
import ru.kubsu.borshchevyk.message.domain.exception.ChatNotFoundException;
import ru.kubsu.borshchevyk.message.domain.exception.ForbiddenActionException;
import ru.kubsu.borshchevyk.message.domain.exception.UserNotInChatException;
import ru.kubsu.borshchevyk.message.domain.model.chat.Chat;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatMember;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatType;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClearChatHistoryService implements ClearChatHistoryUseCase {

    private final LoadChatMemberPort loadChatMemberPort;
    private final LoadChatMembersPort loadChatMembersPort;

    private final SaveChatMembersPort saveChatMembersPort;

    private final LoadChatPort loadChatPort;

    private final PublishChatEventPort publishChatEventPort;

    @Override
    @Transactional
    public void clearChatHistory(ClearChatHistoryCommand command) {
        log.info("Clearing chat history for chat: {} by user: {}", command.chatId(), command.requesterId());

        ChatId chatId = new ChatId(command.chatId());
        UserId requesterId = new UserId(command.requesterId());

        Chat chat = loadChatPort.findById(chatId)
                .orElseThrow(() -> new ChatNotFoundException("Chat not found with id: " + command.chatId()));

        ChatMember requester = loadChatMemberPort.findByChatIdAndUserId(chatId, requesterId)
                .orElseThrow(() -> new UserNotInChatException("Requester is not in the chat"));

        if (!command.forAll()) {
            requester.setHistoryClearedAt(LocalDateTime.now());
            saveChatMembersPort.saveAll(List.of(requester));
            publishChatEventPort.publishChatEvent(requesterId, chatId, "HISTORY_CLEARED");
        } else {
            if (chat.getType() != ChatType.PRIVATE) {
                throw new ForbiddenActionException("Clearing history for all is only allowed in private chats");
            }
            List<ChatMember> allMembers = loadChatMembersPort.findByChatId(chatId);
            LocalDateTime now = LocalDateTime.now();
            allMembers.forEach(member -> {
                member.setHistoryClearedAt(now);
                publishChatEventPort.publishChatEvent(member.getUserId(), chatId, "HISTORY_CLEARED");
            });
            saveChatMembersPort.saveAll(allMembers);
        }
    }
}