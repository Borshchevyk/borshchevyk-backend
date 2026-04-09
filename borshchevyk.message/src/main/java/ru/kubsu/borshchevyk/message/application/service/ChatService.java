package ru.kubsu.borshchevyk.message.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kubsu.borshchevyk.message.application.dto.command.ClearChatHistoryCommand;
import ru.kubsu.borshchevyk.message.application.dto.command.CreateChatCommand;
import ru.kubsu.borshchevyk.message.application.dto.command.DeleteChatCommand;
import ru.kubsu.borshchevyk.message.application.dto.command.UpdatePermissionsCommand;
import ru.kubsu.borshchevyk.message.application.port.in.ClearChatHistoryUseCase;
import ru.kubsu.borshchevyk.message.application.port.in.CreateChatUseCase;
import ru.kubsu.borshchevyk.message.application.port.in.DeleteChatUseCase;
import ru.kubsu.borshchevyk.message.application.port.in.UpdateMemberPermissionsUseCase;
import ru.kubsu.borshchevyk.message.application.port.out.ChatMemberPort;
import ru.kubsu.borshchevyk.message.application.port.out.ChatPort;
import ru.kubsu.borshchevyk.message.domain.exception.ChatNotFoundException;
import ru.kubsu.borshchevyk.message.domain.exception.ForbiddenActionException;
import ru.kubsu.borshchevyk.message.domain.exception.UserNotInChatException;
import ru.kubsu.borshchevyk.message.domain.model.chat.Chat;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatMember;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatRole;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService implements CreateChatUseCase, UpdateMemberPermissionsUseCase, ClearChatHistoryUseCase, DeleteChatUseCase {

    private final ChatPort chatPort;
    private final ChatMemberPort chatMemberPort;

    @Override
    @Transactional
    public Chat createChat(CreateChatCommand command) {
        log.info("Creating chat with title: {}", command.getTitle());

        Chat chat = Chat.builder()
                .id(new ChatId(UUID.randomUUID()))
                .type(command.getType())
                .title(command.getTitle())
                .description(command.getDescription())
                .createdAt(LocalDateTime.now())
                .build();

        chat = chatPort.save(chat);

        List<ChatMember> members = new ArrayList<>();

        ChatMember creator = ChatMember.builder()
                .chatId(chat.getId())
                .userId(new UserId(command.getCreatorId()))
                .role(ChatRole.OWNER)
                .joinedAt(LocalDateTime.now())
                .build();
        members.add(creator);

        if (command.getInitialMemberIds() != null) {
            for (UUID memberId : command.getInitialMemberIds()) {
                if (!memberId.equals(command.getCreatorId())) {
                    members.add(ChatMember.builder()
                            .chatId(chat.getId())
                            .userId(new UserId(memberId))
                            .role(ChatRole.MEMBER)
                            .joinedAt(LocalDateTime.now())
                            .build());
                }
            }
        }

        chatMemberPort.saveAll(members);

        log.info("Chat created successfully with ID: {}", chat.getId().value());
        return chat;
    }

    @Override
    @Transactional
    public void updatePermissions(UpdatePermissionsCommand command) {
        log.info("Updating permissions for user {} in chat {}", command.getTargetUserId(), command.getChatId());

        ChatId chatId = new ChatId(command.getChatId());
        UserId requesterId = new UserId(command.getRequesterId());
        UserId targetUserId = new UserId(command.getTargetUserId());

        ChatMember requester = chatMemberPort.findByChatIdAndUserId(chatId, requesterId)
                .orElseThrow(() -> new UserNotInChatException("Requester is not in the chat"));

        if (requester.getRole() == ChatRole.MEMBER) {
            throw new ForbiddenActionException("MEMBER cannot change permissions");
        }

        ChatMember target = chatMemberPort.findByChatIdAndUserId(chatId, targetUserId)
                .orElseThrow(() -> new UserNotInChatException("Target user is not in the chat"));

        if (requester.getRole() == ChatRole.ADMIN) {
            if (target.getRole() == ChatRole.OWNER || target.getRole() == ChatRole.ADMIN) {
                throw new ForbiddenActionException("ADMIN can only change permissions of MEMBERs");
            }
            if (command.getCanSendMessages() != null && command.getCanSendMessages() && !requester.isCanSendMessages()) {
                throw new ForbiddenActionException("ADMIN cannot grant canSendMessages permission if they don't have it");
            }
            if (command.getCanDeleteMessages() != null && command.getCanDeleteMessages() && !requester.isCanDeleteMessages()) {
                throw new ForbiddenActionException("ADMIN cannot grant canDeleteMessages permission if they don't have it");
            }
            if (command.getCanInviteUsers() != null && command.getCanInviteUsers() && !requester.isCanInviteUsers()) {
                throw new ForbiddenActionException("ADMIN cannot grant canInviteUsers permission if they don't have it");
            }
            if (command.getCanChangeInfo() != null && command.getCanChangeInfo() && !requester.isCanChangeInfo()) {
                throw new ForbiddenActionException("ADMIN cannot grant canChangeInfo permission if they don't have it");
            }
        }

        if (command.getCanSendMessages() != null) target.setCanSendMessages(command.getCanSendMessages());
        if (command.getCanDeleteMessages() != null) target.setCanDeleteMessages(command.getCanDeleteMessages());
        if (command.getCanInviteUsers() != null) target.setCanInviteUsers(command.getCanInviteUsers());
        if (command.getCanChangeInfo() != null) target.setCanChangeInfo(command.getCanChangeInfo());

        chatMemberPort.saveAll(List.of(target));
    }

    @Override
    @Transactional
    public void clearChatHistory(ClearChatHistoryCommand command) {
        log.info("Clearing chat history for chat: {} by user: {}", command.getChatId(), command.getRequesterId());

        ChatId chatId = new ChatId(command.getChatId());
        UserId requesterId = new UserId(command.getRequesterId());

        Chat chat = chatPort.findById(chatId)
                .orElseThrow(() -> new ChatNotFoundException("Chat not found with id: " + command.getChatId()));

        ChatMember requester = chatMemberPort.findByChatIdAndUserId(chatId, requesterId)
                .orElseThrow(() -> new UserNotInChatException("Requester is not in the chat"));

        if (!command.isForAll()) {
            requester.setHistoryClearedAt(LocalDateTime.now());
            chatMemberPort.saveAll(List.of(requester));
        } else {
            if (chat.getType() != ru.kubsu.borshchevyk.message.domain.model.chat.ChatType.PRIVATE) {
                throw new ForbiddenActionException("Clearing history for all is only allowed in private chats");
            }
            List<ChatMember> allMembers = chatMemberPort.findByChatId(chatId);
            LocalDateTime now = LocalDateTime.now();
            allMembers.forEach(member -> member.setHistoryClearedAt(now));
            chatMemberPort.saveAll(allMembers);
        }
    }

    @Override
    @Transactional
    public void deleteChat(DeleteChatCommand command) {
        log.info("Deleting chat: {} by user: {}", command.getChatId(), command.getRequesterId());

        ChatId chatId = new ChatId(command.getChatId());
        UserId requesterId = new UserId(command.getRequesterId());

        Chat chat = chatPort.findById(chatId)
                .orElseThrow(() -> new ChatNotFoundException("Chat not found with id: " + command.getChatId()));

        ChatMember requester = chatMemberPort.findByChatIdAndUserId(chatId, requesterId)
                .orElseThrow(() -> new UserNotInChatException("Requester is not in the chat"));

        if (chat.getType() == ru.kubsu.borshchevyk.message.domain.model.chat.ChatType.PRIVATE) {
            chat.setDeleted(true);
            chatPort.save(chat);
        } else {
            if (requester.getRole() != ChatRole.OWNER) {
                throw new ForbiddenActionException("Only OWNER can delete group or channel chats");
            }
            chat.setDeleted(true);
            chatPort.save(chat);
        }
    }
}
