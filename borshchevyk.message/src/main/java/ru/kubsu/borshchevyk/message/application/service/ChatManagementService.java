package ru.kubsu.borshchevyk.message.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kubsu.borshchevyk.message.application.dto.command.ClearChatHistoryCommand;
import ru.kubsu.borshchevyk.message.application.dto.command.DeleteChatCommand;
import ru.kubsu.borshchevyk.message.application.dto.command.UpdateChatInfoCommand;
import ru.kubsu.borshchevyk.message.application.port.in.ClearChatHistoryUseCase;
import ru.kubsu.borshchevyk.message.application.port.in.DeleteChatUseCase;
import ru.kubsu.borshchevyk.message.application.port.in.GenerateInviteLinkUseCase;
import ru.kubsu.borshchevyk.message.application.port.in.JoinChatByLinkUseCase;
import ru.kubsu.borshchevyk.message.application.port.in.UpdateChatInfoUseCase;
import ru.kubsu.borshchevyk.message.application.port.out.ChatMemberPort;
import ru.kubsu.borshchevyk.message.application.port.out.ChatPort;
import ru.kubsu.borshchevyk.message.domain.exception.ChatNotFoundException;
import ru.kubsu.borshchevyk.message.domain.exception.ForbiddenActionException;
import ru.kubsu.borshchevyk.message.domain.exception.UserNotInChatException;
import ru.kubsu.borshchevyk.message.domain.model.chat.Channel;
import ru.kubsu.borshchevyk.message.domain.model.chat.Chat;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatMember;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatRole;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatType;
import ru.kubsu.borshchevyk.message.domain.model.chat.GroupChat;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import ru.kubsu.borshchevyk.message.application.port.in.PinChatUseCase;
import ru.kubsu.borshchevyk.message.application.port.in.UnpinChatUseCase;
import ru.kubsu.borshchevyk.message.application.dto.command.PinChatCommand;
import ru.kubsu.borshchevyk.message.application.dto.command.UnpinChatCommand;
import ru.kubsu.borshchevyk.message.application.port.out.RealtimeNotificationPort;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatManagementService implements ClearChatHistoryUseCase, DeleteChatUseCase, GenerateInviteLinkUseCase, JoinChatByLinkUseCase, UpdateChatInfoUseCase, PinChatUseCase, UnpinChatUseCase {

    private final ChatPort chatPort;
    private final ChatMemberPort chatMemberPort;
    private final RealtimeNotificationPort realtimeNotificationPort;

    @Override
    @Transactional
    public void pinChat(PinChatCommand command) {
        log.info("Pinning chat {} for user {}", command.getChatId(), command.getRequesterId());
        ChatId chatId = new ChatId(command.getChatId());
        UserId requesterId = new UserId(command.getRequesterId());

        ChatMember requester = chatMemberPort.findByChatIdAndUserId(chatId, requesterId)
                .orElseThrow(() -> new UserNotInChatException("Requester is not in the chat"));

        requester.setPinned(true);
        chatMemberPort.saveAll(List.of(requester));
        realtimeNotificationPort.notifyChatEvent(requesterId, chatId, "PINNED");
    }

    @Override
    @Transactional
    public void unpinChat(UnpinChatCommand command) {
        log.info("Unpinning chat {} for user {}", command.getChatId(), command.getRequesterId());
        ChatId chatId = new ChatId(command.getChatId());
        UserId requesterId = new UserId(command.getRequesterId());

        ChatMember requester = chatMemberPort.findByChatIdAndUserId(chatId, requesterId)
                .orElseThrow(() -> new UserNotInChatException("Requester is not in the chat"));

        requester.setPinned(false);
        chatMemberPort.saveAll(List.of(requester));
        realtimeNotificationPort.notifyChatEvent(requesterId, chatId, "UNPINNED");
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
            realtimeNotificationPort.notifyChatEvent(requesterId, chatId, "HISTORY_CLEARED");
        } else {
            if (chat.getType() != ru.kubsu.borshchevyk.message.domain.model.chat.ChatType.PRIVATE) {
                throw new ForbiddenActionException("Clearing history for all is only allowed in private chats");
            }
            List<ChatMember> allMembers = chatMemberPort.findByChatId(chatId);
            LocalDateTime now = LocalDateTime.now();
            allMembers.forEach(member -> {
                member.setHistoryClearedAt(now);
                realtimeNotificationPort.notifyChatEvent(member.getUserId(), chatId, "HISTORY_CLEARED");
            });
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
            chatMemberPort.findByChatId(chatId).forEach(member -> 
                realtimeNotificationPort.notifyChatEvent(member.getUserId(), chatId, "DELETED")
            );
        } else {
            if (requester.getRole() != ChatRole.OWNER) {
                throw new ForbiddenActionException("Only OWNER can delete group or channel chats");
            }
            chat.setDeleted(true);
            chatPort.save(chat);
            chatMemberPort.findByChatId(chatId).forEach(member ->
                realtimeNotificationPort.notifyChatEvent(member.getUserId(), chatId, "DELETED")
            );
        }
    }

    @Override
    @Transactional
    public String generateInviteLink(UUID chatIdRaw, UUID requesterIdRaw) {
        log.info("Generating invite link for chat {} by {}", chatIdRaw, requesterIdRaw);
        ChatId chatId = new ChatId(chatIdRaw);
        UserId requesterId = new UserId(requesterIdRaw);

        Chat chat = chatPort.findById(chatId)
                .orElseThrow(() -> new ChatNotFoundException("Chat not found"));

        if (chat.getType() == ChatType.PRIVATE || chat.getType() == ChatType.SAVED_MESSAGES) {
            throw new ForbiddenActionException("Cannot generate invite links for this chat type");
        }

        ChatMember requester = chatMemberPort.findByChatIdAndUserId(chatId, requesterId)
                .orElseThrow(() -> new UserNotInChatException("Requester is not in the chat"));

        if (!requester.isCanInviteUsers() && requester.getRole() == ChatRole.MEMBER) {
            throw new ForbiddenActionException("User does not have permission to invite");
        }

        String inviteCode = UUID.randomUUID().toString();
        if (chat instanceof GroupChat groupChat) {
            groupChat.setInviteCode(inviteCode);
        } else if (chat instanceof Channel channel) {
            channel.setInviteCode(inviteCode);
        }
        chatPort.save(chat);

        return inviteCode;
    }

    @Override
    @Transactional
    public Chat joinChatByLink(String inviteCode, UUID userIdRaw) {
        log.info("User {} joining chat by invite link", userIdRaw);
        UserId userId = new UserId(userIdRaw);

        Chat chat = chatPort.findByInviteCode(inviteCode)
                .orElseThrow(() -> new ChatNotFoundException("Chat not found or invalid invite link"));

        if (chat.isDeleted()) {
             throw new ChatNotFoundException("Chat has been deleted");
        }

        Optional<ChatMember> existingMember = chatMemberPort.findByChatIdAndUserId(chat.getId(), userId);
        if (existingMember.isPresent()) {
            return chat;
        }

        ChatMember newMember = ChatMember.builder()
                .chatId(chat.getId())
                .userId(userId)
                .role(ChatRole.MEMBER)
                .joinedAt(LocalDateTime.now())
                .build();

        chatMemberPort.saveAll(List.of(newMember));
        return chat;
    }

    @Override
    @Transactional
    public void updateChatInfo(UpdateChatInfoCommand command) {
        log.info("Updating chat info for chat {} by user {}", command.getChatId(), command.getRequesterId());
        ChatId chatId = new ChatId(command.getChatId());
        UserId requesterId = new UserId(command.getRequesterId());

        Chat chat = chatPort.findById(chatId)
                .orElseThrow(() -> new ChatNotFoundException("Chat not found"));

        if (chat.getType() == ChatType.PRIVATE || chat.getType() == ChatType.SAVED_MESSAGES) {
            throw new ForbiddenActionException("Cannot update info of private or saved messages chats");
        }

        ChatMember requester = chatMemberPort.findByChatIdAndUserId(chatId, requesterId)
                .orElseThrow(() -> new UserNotInChatException("Requester is not in the chat"));

        if (!requester.isCanChangeInfo() && requester.getRole() == ChatRole.MEMBER) {
            throw new ForbiddenActionException("User does not have permission to change chat info");
        }

        if (chat instanceof GroupChat groupChat) {
            if (command.getTitle() != null && !command.getTitle().isBlank()) {
                groupChat.setTitle(command.getTitle());
            }
            if (command.getDescription() != null) {
                groupChat.setDescription(command.getDescription());
            }
            if (command.getCommentsEnabled() != null) {
                groupChat.setCommentsEnabled(command.getCommentsEnabled());
            }
        } else if (chat instanceof Channel channel) {
            if (command.getTitle() != null && !command.getTitle().isBlank()) {
                channel.setTitle(command.getTitle());
            }
            if (command.getDescription() != null) {
                channel.setDescription(command.getDescription());
            }
            if (command.getCommentsEnabled() != null) {
                channel.setCommentsEnabled(command.getCommentsEnabled());
            }
        }

        chatPort.save(chat);
    }
}
