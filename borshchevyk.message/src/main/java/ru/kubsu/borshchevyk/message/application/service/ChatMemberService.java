package ru.kubsu.borshchevyk.message.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kubsu.borshchevyk.message.application.dto.command.InviteUserCommand;
import ru.kubsu.borshchevyk.message.application.dto.command.KickUserCommand;
import ru.kubsu.borshchevyk.message.application.dto.command.LeaveChatCommand;
import ru.kubsu.borshchevyk.message.application.dto.command.UpdatePermissionsCommand;
import ru.kubsu.borshchevyk.message.application.port.in.InviteUserUseCase;
import ru.kubsu.borshchevyk.message.application.port.in.KickUserUseCase;
import ru.kubsu.borshchevyk.message.application.port.in.LeaveChatUseCase;
import ru.kubsu.borshchevyk.message.application.port.in.LoadChatMembersUseCase;
import ru.kubsu.borshchevyk.message.application.port.in.UpdateMemberPermissionsUseCase;
import ru.kubsu.borshchevyk.message.application.port.out.ChatMemberPort;
import ru.kubsu.borshchevyk.message.application.port.out.ChatPort;
import ru.kubsu.borshchevyk.message.application.port.out.CheckUserPrivacyPort;
import ru.kubsu.borshchevyk.message.domain.exception.ChatNotFoundException;
import ru.kubsu.borshchevyk.message.domain.exception.ForbiddenActionException;
import ru.kubsu.borshchevyk.message.domain.exception.UserNotInChatException;
import ru.kubsu.borshchevyk.message.domain.model.chat.Chat;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatMember;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatRole;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatType;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * ChatMemberService implementation.
 *
 * @author Aleksey Timko
 * @since 2026-05-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatMemberService implements UpdateMemberPermissionsUseCase, InviteUserUseCase, KickUserUseCase, LeaveChatUseCase, LoadChatMembersUseCase {

    private final ChatPort chatPort;
    private final ChatMemberPort chatMemberPort;
    private final CheckUserPrivacyPort checkUserPrivacyPort;

    @Override
    @Transactional
    public void updatePermissions(UpdatePermissionsCommand command) {
        log.info("Updating permissions for user {} in chat {}", command.targetUserId(), command.chatId());

        ChatId chatId = new ChatId(command.chatId());
        UserId requesterId = new UserId(command.requesterId());
        UserId targetUserId = new UserId(command.targetUserId());

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
            if (command.canSendMessages() != null && command.canSendMessages() && !requester.isCanSendMessages()) {
                throw new ForbiddenActionException("ADMIN cannot grant canSendMessages permission if they don't have it");
            }
            if (command.canDeleteMessages() != null && command.canDeleteMessages() && !requester.isCanDeleteMessages()) {
                throw new ForbiddenActionException("ADMIN cannot grant canDeleteMessages permission if they don't have it");
            }
            if (command.canInviteUsers() != null && command.canInviteUsers() && !requester.isCanInviteUsers()) {
                throw new ForbiddenActionException("ADMIN cannot grant canInviteUsers permission if they don't have it");
            }
            if (command.canChangeInfo() != null && command.canChangeInfo() && !requester.isCanChangeInfo()) {
                throw new ForbiddenActionException("ADMIN cannot grant canChangeInfo permission if they don't have it");
            }
        }

        if (command.canSendMessages() != null) target.setCanSendMessages(command.canSendMessages());
        if (command.canDeleteMessages() != null) target.setCanDeleteMessages(command.canDeleteMessages());
        if (command.canInviteUsers() != null) target.setCanInviteUsers(command.canInviteUsers());
        if (command.canChangeInfo() != null) target.setCanChangeInfo(command.canChangeInfo());

        chatMemberPort.saveAll(List.of(target));
    }

    @Override
    @Transactional
    public void inviteUser(InviteUserCommand command) {
        log.info("Inviting user {} to chat {} by {}", command.targetUserId(), command.chatId(), command.requesterId());
        ChatId chatId = new ChatId(command.chatId());
        UserId requesterId = new UserId(command.requesterId());
        UserId targetUserId = new UserId(command.targetUserId());

        Chat chat = chatPort.findById(chatId)
                .orElseThrow(() -> new ChatNotFoundException("Chat not found"));

        if (chat.getType() == ChatType.PRIVATE || chat.getType() == ChatType.SAVED_MESSAGES) {
            throw new ForbiddenActionException("Cannot invite users to private or saved messages chats");
        }

        ChatMember requester = chatMemberPort.findByChatIdAndUserId(chatId, requesterId)
                .orElseThrow(() -> new UserNotInChatException("Requester is not in the chat"));

        if (!requester.isCanInviteUsers() && requester.getRole() == ChatRole.MEMBER) {
            throw new ForbiddenActionException("User does not have permission to invite");
        }
        
        if (!checkUserPrivacyPort.canInviteToChat(targetUserId.value(), requesterId.value())) {
            throw new ForbiddenActionException("User's privacy settings do not allow you to invite them");
        }

        Optional<ChatMember> existingMember = chatMemberPort.findByChatIdAndUserId(chatId, targetUserId);
        if (existingMember.isPresent()) {
            return; // Already a member
        }

        ChatMember newMember = ChatMember.builder()
                .chatId(chatId)
                .userId(targetUserId)
                .role(ChatRole.MEMBER)
                .joinedAt(LocalDateTime.now())
                .lastReadAt(LocalDateTime.now())
                .build();

        chatMemberPort.saveAll(List.of(newMember));
    }

    @Override
    @Transactional
    public void kickUser(KickUserCommand command) {
        log.info("Kicking user {} from chat {} by {}", command.targetUserId(), command.chatId(), command.requesterId());
        ChatId chatId = new ChatId(command.chatId());
        UserId requesterId = new UserId(command.requesterId());
        UserId targetUserId = new UserId(command.targetUserId());

        ChatMember requester = chatMemberPort.findByChatIdAndUserId(chatId, requesterId)
                .orElseThrow(() -> new UserNotInChatException("Requester is not in the chat"));

        if (requester.getRole() == ChatRole.MEMBER) {
            throw new ForbiddenActionException("MEMBER cannot kick users");
        }

        ChatMember target = chatMemberPort.findByChatIdAndUserId(chatId, targetUserId)
                .orElseThrow(() -> new UserNotInChatException("Target user is not in the chat"));

        if (requester.getRole() == ChatRole.ADMIN && (target.getRole() == ChatRole.ADMIN || target.getRole() == ChatRole.OWNER)) {
            throw new ForbiddenActionException("ADMIN cannot kick other ADMINs or OWNERs");
        }

        if (requesterId.equals(targetUserId)) {
            throw new ForbiddenActionException("Use leaveChat instead of kickUser to kick yourself");
        }

        chatMemberPort.delete(target);
    }

    @Override
    @Transactional
    public void leaveChat(LeaveChatCommand command) {
        log.info("User {} leaving chat {}", command.requesterId(), command.chatId());
        ChatId chatId = new ChatId(command.chatId());
        UserId requesterId = new UserId(command.requesterId());

        Chat chat = chatPort.findById(chatId)
                .orElseThrow(() -> new ChatNotFoundException("Chat not found"));

        ChatMember requester = chatMemberPort.findByChatIdAndUserId(chatId, requesterId)
                .orElseThrow(() -> new UserNotInChatException("Requester is not in the chat"));

        if (chat.getType() == ChatType.PRIVATE || chat.getType() == ChatType.SAVED_MESSAGES) {
             throw new ForbiddenActionException("Cannot leave private or saved messages chats");
        }

        chatMemberPort.delete(requester);

        List<ChatMember> remainingMembers = chatMemberPort.findByChatId(chatId);
        if (remainingMembers.isEmpty()) {
            chat.setDeleted(true);
            chatPort.save(chat);
        } else if (requester.getRole() == ChatRole.OWNER) {
            // Assign new owner (first joined)
            remainingMembers.stream()
                .min(java.util.Comparator.comparing(ChatMember::getJoinedAt))
                .ifPresent(newOwner -> {
                    newOwner.setRole(ChatRole.OWNER);
                    chatMemberPort.saveAll(List.of(newOwner));
                });
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ChatMember> loadChatMembers(UUID chatIdRaw, UUID requesterIdRaw, Pageable pageable) {
        log.info("Loading members for chat {} by user {}", chatIdRaw, requesterIdRaw);
        ChatId chatId = new ChatId(chatIdRaw);
        UserId requesterId = new UserId(requesterIdRaw);

        Chat chat = chatPort.findById(chatId)
                .orElseThrow(() -> new ChatNotFoundException("Chat not found"));

        chatMemberPort.findByChatIdAndUserId(chatId, requesterId)
                .orElseThrow(() -> new UserNotInChatException("Requester is not in the chat"));

        return chatMemberPort.findByChatId(chatId, pageable);
    }
}