package ru.kubsu.borshchevyk.message.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kubsu.borshchevyk.message.application.dto.command.LeaveChatCommand;
import ru.kubsu.borshchevyk.message.application.port.in.LeaveChatUseCase;
import ru.kubsu.borshchevyk.message.application.port.out.*;
import ru.kubsu.borshchevyk.message.domain.exception.ChatNotFoundException;
import ru.kubsu.borshchevyk.message.domain.exception.UserNotInChatException;
import ru.kubsu.borshchevyk.message.domain.model.chat.Chat;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatMember;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatRole;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LeaveChatService implements LeaveChatUseCase {

    private final SaveChatPort saveChatPort;
    private final SaveChatMembersPort saveChatMembersPort;
    private final LoadChatPort loadChatPort;
    private final LoadChatMemberPort loadChatMemberPort;
    private final LoadChatMembersPort loadChatMembersPort;
    private final DeleteChatMemberPort deleteChatMemberPort;

    @Override
    @Transactional
    public void leaveChat(LeaveChatCommand command) {
        log.info("User {} leaving chat {}", command.requesterId(), command.chatId());
        ChatId chatId = new ChatId(command.chatId());
        UserId requesterId = new UserId(command.requesterId());

        Chat chat = loadChatPort.findById(chatId)
                .orElseThrow(() -> new ChatNotFoundException("Chat not found"));

        ChatMember requester = loadChatMemberPort.findByChatIdAndUserId(chatId, requesterId)
                .orElseThrow(() -> new UserNotInChatException("Requester is not in the chat"));

        chat.validateCanLeaveChat();

        deleteChatMemberPort.delete(requester);

        List<ChatMember> remainingMembers = loadChatMembersPort.findByChatId(chatId);
        if (remainingMembers.isEmpty()) {
            chat.setDeleted(true);
            saveChatPort.save(chat);
        } else if (requester.getRole() == ChatRole.OWNER) {
            remainingMembers.stream()
                .min(java.util.Comparator.comparing(ChatMember::getJoinedAt))
                .ifPresent(newOwner -> {
                    newOwner.setRole(ChatRole.OWNER);
                    saveChatMembersPort.saveAll(List.of(newOwner));
                });
        }
    }
}