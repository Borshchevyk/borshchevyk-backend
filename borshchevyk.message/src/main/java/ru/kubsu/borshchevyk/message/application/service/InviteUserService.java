package ru.kubsu.borshchevyk.message.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kubsu.borshchevyk.message.application.dto.command.InviteUserCommand;
import ru.kubsu.borshchevyk.message.application.port.in.InviteUserUseCase;
import ru.kubsu.borshchevyk.message.application.port.out.CheckUserPrivacyPort;
import ru.kubsu.borshchevyk.message.application.port.out.LoadChatMemberPort;
import ru.kubsu.borshchevyk.message.application.port.out.LoadChatPort;
import ru.kubsu.borshchevyk.message.application.port.out.SaveChatMembersPort;
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

import ru.kubsu.borshchevyk.message.domain.model.chat.member_permissions.ChatMemberPermissions.PermissionType;

@Slf4j
@Service
@RequiredArgsConstructor
public class InviteUserService implements InviteUserUseCase {

    private final LoadChatMemberPort loadChatMemberPort;
    private final SaveChatMembersPort saveChatMembersPort;
    private final LoadChatPort loadChatPort;

    private final CheckUserPrivacyPort checkUserPrivacyPort;

    @Override
    @Transactional
    public void inviteUser(InviteUserCommand command) {
        log.info("Inviting user {} to chat {} by {}", command.targetUserId(), command.chatId(), command.requesterId());
        ChatId chatId = new ChatId(command.chatId());
        UserId requesterId = new UserId(command.requesterId());
        UserId targetUserId = new UserId(command.targetUserId());

        Chat chat = loadChatPort.findById(chatId)
                .orElseThrow(() -> new ChatNotFoundException("Chat not found"));

        if (chat.getType() == ChatType.PRIVATE || chat.getType() == ChatType.SAVED_MESSAGES) {
            throw new ForbiddenActionException("Cannot invite users to private or saved messages chats");
        }

        ChatMember requester = loadChatMemberPort.findByChatIdAndUserId(chatId, requesterId)
                .orElseThrow(() -> new UserNotInChatException("Requester is not in the chat"));

        if (!requester.getPermissions().hasPermission(ru.kubsu.borshchevyk.message.domain.model.chat.member_permissions.ChatMemberPermissions.PermissionType.INVITE_USERS) && requester.getRole() == ChatRole.MEMBER) {
            throw new ForbiddenActionException("User does not have permission to invite");
        }
        
        if (checkUserPrivacyPort.canInviteToChat(targetUserId.value(), requesterId.value())) {
            throw new ForbiddenActionException("User's privacy settings do not allow you to invite them");
        }

        Optional<ChatMember> existingMember = loadChatMemberPort.findByChatIdAndUserId(chatId, targetUserId);
        if (existingMember.isPresent()) {
            return;
        }

        ChatMember newMember = ChatMember.builder()
                .chatId(chatId)
                .userId(targetUserId)
                .role(ChatRole.MEMBER)
                .joinedAt(LocalDateTime.now())
                .lastReadAt(LocalDateTime.now())
                .build();

        saveChatMembersPort.saveAll(List.of(newMember));
    }
}