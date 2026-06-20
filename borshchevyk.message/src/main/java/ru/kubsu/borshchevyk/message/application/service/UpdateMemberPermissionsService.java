package ru.kubsu.borshchevyk.message.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kubsu.borshchevyk.message.application.dto.command.UpdatePermissionsCommand;
import ru.kubsu.borshchevyk.message.application.port.in.UpdateMemberPermissionsUseCase;
import ru.kubsu.borshchevyk.message.domain.exception.ForbiddenActionException;
import ru.kubsu.borshchevyk.message.domain.exception.UserNotInChatException;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatMember;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatRole;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;
import ru.kubsu.borshchevyk.message.application.port.out.LoadChatMemberPort;
import ru.kubsu.borshchevyk.message.application.port.out.SaveChatMembersPort;
import ru.kubsu.borshchevyk.message.domain.model.chat.member_permissions.ChatMemberPermissions.PermissionType;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateMemberPermissionsService implements UpdateMemberPermissionsUseCase {

    private final LoadChatMemberPort loadChatMemberPort;
    private final SaveChatMembersPort saveChatMembersPort;

    @Override
    @Transactional
    public void updatePermissions(UpdatePermissionsCommand command) {
        log.info("Updating permissions for user {} in chat {}", command.targetUserId(), command.chatId());

        ChatId chatId = new ChatId(command.chatId());
        UserId requesterId = new UserId(command.requesterId());
        UserId targetUserId = new UserId(command.targetUserId());

        ChatMember requester = loadChatMemberPort.findByChatIdAndUserId(chatId, requesterId)
                .orElseThrow(() -> new UserNotInChatException("Requester is not in the chat"));

        if (requester.getRole() == ChatRole.MEMBER) {
            throw new ForbiddenActionException("MEMBER cannot change permissions");
        }

        ChatMember target = loadChatMemberPort.findByChatIdAndUserId(chatId, targetUserId)
                .orElseThrow(() -> new UserNotInChatException("Target user is not in the chat"));

        if (requester.getRole() == ChatRole.ADMIN) {
            if (target.getRole() == ChatRole.OWNER || target.getRole() == ChatRole.ADMIN) {
                throw new ForbiddenActionException("ADMIN can only change permissions of MEMBERs");
            }
            if (command.canSendMessages() != null && command.canSendMessages() && !requester.getPermissions().hasPermission(PermissionType.SEND_MESSAGES)) {
                throw new ForbiddenActionException("ADMIN cannot grant canSendMessages permission if they don't have it");
            }
            if (command.canDeleteMessages() != null && command.canDeleteMessages() && !requester.getPermissions().hasPermission(PermissionType.DELETE_MESSAGES)) {
                throw new ForbiddenActionException("ADMIN cannot grant canDeleteMessages permission if they don't have it");
            }
            if (command.canInviteUsers() != null && command.canInviteUsers() && !requester.getPermissions().hasPermission(PermissionType.INVITE_USERS)) {
                throw new ForbiddenActionException("ADMIN cannot grant canInviteUsers permission if they don't have it");
            }
            if (command.canChangeInfo() != null && command.canChangeInfo() && !requester.getPermissions().hasPermission(PermissionType.CHANGE_CHAT_INFO)) {
                throw new ForbiddenActionException("ADMIN cannot grant canChangeInfo permission if they don't have it");
            }
        }

        if (command.canSendMessages() != null) {
            if (command.canSendMessages()) target.getPermissions().addPermission(PermissionType.SEND_MESSAGES);
            else target.getPermissions().revokePermission(PermissionType.SEND_MESSAGES);
        }
        if (command.canDeleteMessages() != null) {
            if (command.canDeleteMessages()) target.getPermissions().addPermission(PermissionType.DELETE_MESSAGES);
            else target.getPermissions().revokePermission(PermissionType.DELETE_MESSAGES);
        }
        if (command.canInviteUsers() != null) {
            if (command.canInviteUsers()) target.getPermissions().addPermission(PermissionType.INVITE_USERS);
            else target.getPermissions().revokePermission(PermissionType.INVITE_USERS);
        }
        if (command.canChangeInfo() != null) {
            if (command.canChangeInfo()) target.getPermissions().addPermission(PermissionType.CHANGE_CHAT_INFO);
            else target.getPermissions().revokePermission(PermissionType.CHANGE_CHAT_INFO);
        }

        saveChatMembersPort.saveAll(List.of(target));
    }
}