package ru.kubsu.borshchevyk.message.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kubsu.borshchevyk.message.application.dto.command.ClearChatHistoryCommand;
import ru.kubsu.borshchevyk.message.application.dto.command.CreateChatCommand;
import ru.kubsu.borshchevyk.message.application.dto.command.DeleteChatCommand;
import ru.kubsu.borshchevyk.message.application.dto.command.InviteUserCommand;
import ru.kubsu.borshchevyk.message.application.dto.command.KickUserCommand;
import ru.kubsu.borshchevyk.message.application.dto.command.LeaveChatCommand;
import ru.kubsu.borshchevyk.message.application.dto.command.UpdatePermissionsCommand;
import ru.kubsu.borshchevyk.message.application.port.in.ClearChatHistoryUseCase;
import ru.kubsu.borshchevyk.message.application.port.in.CreateChatUseCase;
import ru.kubsu.borshchevyk.message.application.port.in.CreatePrivateChatUseCase;
import ru.kubsu.borshchevyk.message.application.port.in.DeleteChatUseCase;
import ru.kubsu.borshchevyk.message.application.port.in.GenerateInviteLinkUseCase;
import ru.kubsu.borshchevyk.message.application.port.in.InviteUserUseCase;
import ru.kubsu.borshchevyk.message.application.port.in.JoinChatByLinkUseCase;
import ru.kubsu.borshchevyk.message.application.port.in.KickUserUseCase;
import ru.kubsu.borshchevyk.message.application.port.in.LeaveChatUseCase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.kubsu.borshchevyk.message.application.dto.command.UpdateChatInfoCommand;
import ru.kubsu.borshchevyk.message.application.port.in.LoadChatMembersUseCase;
import ru.kubsu.borshchevyk.message.application.port.in.LoadUserChatsUseCase;
import ru.kubsu.borshchevyk.message.application.port.in.UpdateChatInfoUseCase;
import ru.kubsu.borshchevyk.message.application.port.in.UpdateMemberPermissionsUseCase;
import ru.kubsu.borshchevyk.message.application.port.out.ChatMemberPort;
import ru.kubsu.borshchevyk.message.application.port.out.ChatPort;
import ru.kubsu.borshchevyk.message.domain.exception.ChatNotFoundException;
import ru.kubsu.borshchevyk.message.domain.exception.ForbiddenActionException;
import ru.kubsu.borshchevyk.message.domain.exception.UserNotInChatException;
import ru.kubsu.borshchevyk.message.domain.model.chat.Chat;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatMember;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatRole;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatType;
import ru.kubsu.borshchevyk.message.domain.model.chat.PrivateChat;
import ru.kubsu.borshchevyk.message.domain.model.chat.GroupChat;
import ru.kubsu.borshchevyk.message.domain.model.chat.Channel;
import ru.kubsu.borshchevyk.message.domain.model.chat.SavedMessages;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService implements CreateChatUseCase, CreatePrivateChatUseCase, UpdateMemberPermissionsUseCase, ClearChatHistoryUseCase, DeleteChatUseCase, LoadUserChatsUseCase, InviteUserUseCase, KickUserUseCase, LeaveChatUseCase, GenerateInviteLinkUseCase, JoinChatByLinkUseCase, UpdateChatInfoUseCase, LoadChatMembersUseCase {

    private final ChatPort chatPort;
    private final ChatMemberPort chatMemberPort;

    @Override
    @Transactional
    public Chat createPrivateChat(UUID requesterId, UUID targetUserId) {
        log.info("Creating private chat between {} and {}", requesterId, targetUserId);
        UserId u1 = new UserId(requesterId);
        UserId u2 = new UserId(targetUserId);

        if (requesterId.equals(targetUserId)) {
            throw new IllegalArgumentException("Cannot create private chat with yourself. Use Saved Messages.");
        }

        Optional<Chat> existingChat = chatPort.findPrivateChatBetweenUsers(u1, u2);
        if (existingChat.isPresent()) {
            return existingChat.get();
        }

        Chat chat = PrivateChat.builder()
                .id(new ChatId(UUID.randomUUID()))
                .type(ChatType.PRIVATE)
                .createdAt(LocalDateTime.now())
                .build();
        chat = chatPort.save(chat);

        ChatMember member1 = ChatMember.builder()
                .chatId(chat.getId())
                .userId(u1)
                .role(ChatRole.MEMBER)
                .joinedAt(LocalDateTime.now())
                .build();

        ChatMember member2 = ChatMember.builder()
                .chatId(chat.getId())
                .userId(u2)
                .role(ChatRole.MEMBER)
                .joinedAt(LocalDateTime.now())
                .build();

        chatMemberPort.saveAll(List.of(member1, member2));

        return chat;
    }

    @Override
    @Transactional
    public Chat createChat(CreateChatCommand command) {
        log.info("Creating chat with title: {}", command.getTitle());

        Chat chat;
        ChatId newChatId = new ChatId(UUID.randomUUID());
        if (command.getType() == ChatType.GROUP) {
            chat = GroupChat.builder()
                    .id(newChatId)
                    .type(command.getType())
                    .title(command.getTitle())
                    .description(command.getDescription())
                    .createdAt(LocalDateTime.now())
                    .build();
        } else if (command.getType() == ChatType.CHANNEL) {
            chat = Channel.builder()
                    .id(newChatId)
                    .type(command.getType())
                    .title(command.getTitle())
                    .description(command.getDescription())
                    .createdAt(LocalDateTime.now())
                    .build();
        } else {
             throw new IllegalArgumentException("Unsupported chat type for general creation: " + command.getType());
        }

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
    public List<Chat> loadUserChats(UserId userId) {
        log.info("Loading chats for user: {}", userId.value());
        List<ChatMember> members = chatMemberPort.findByUserId(userId);
        if (members.isEmpty()) {
            return new ArrayList<>();
        }
        List<ChatId> chatIds = members.stream()
                .map(ChatMember::getChatId)
                .collect(Collectors.toList());
        List<Chat> chats = chatPort.findByIdIn(chatIds);
        return chats.stream()
                .filter(chat -> !chat.isDeleted())
                .collect(Collectors.toList());
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

    @Override
    @Transactional
    public void inviteUser(InviteUserCommand command) {
        log.info("Inviting user {} to chat {} by {}", command.getTargetUserId(), command.getChatId(), command.getRequesterId());
        ChatId chatId = new ChatId(command.getChatId());
        UserId requesterId = new UserId(command.getRequesterId());
        UserId targetUserId = new UserId(command.getTargetUserId());

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

        Optional<ChatMember> existingMember = chatMemberPort.findByChatIdAndUserId(chatId, targetUserId);
        if (existingMember.isPresent()) {
            return; // Already a member
        }

        ChatMember newMember = ChatMember.builder()
                .chatId(chatId)
                .userId(targetUserId)
                .role(ChatRole.MEMBER)
                .joinedAt(LocalDateTime.now())
                .build();

        chatMemberPort.saveAll(List.of(newMember));
    }

    @Override
    @Transactional
    public void kickUser(KickUserCommand command) {
        log.info("Kicking user {} from chat {} by {}", command.getTargetUserId(), command.getChatId(), command.getRequesterId());
        ChatId chatId = new ChatId(command.getChatId());
        UserId requesterId = new UserId(command.getRequesterId());
        UserId targetUserId = new UserId(command.getTargetUserId());

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
        log.info("User {} leaving chat {}", command.getRequesterId(), command.getChatId());
        ChatId chatId = new ChatId(command.getChatId());
        UserId requesterId = new UserId(command.getRequesterId());

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

    @Override
    @Transactional
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
