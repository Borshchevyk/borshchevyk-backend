package ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.kubsu.borshchevyk.message.domain.model.chat.*;
import ru.kubsu.borshchevyk.message.domain.model.chat.type.Channel;
import ru.kubsu.borshchevyk.message.domain.model.chat.type.GroupChat;
import ru.kubsu.borshchevyk.message.domain.model.chat.type.PrivateChat;
import ru.kubsu.borshchevyk.message.domain.model.chat.type.SavedMessages;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response.*;

import ru.kubsu.borshchevyk.message.domain.model.chat.member_permissions.ChatMemberPermissions;
import ru.kubsu.borshchevyk.message.domain.model.chat.visitor.ChatVisitor;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", imports = {ChatMemberPermissions.class})
public abstract class PresentationChatMapper {

    public ChatResponse toResponse(Chat chat) {
        if (chat == null) {
            return null;
        }

        return chat.accept(new ChatVisitor<ChatResponse>() {
            @Override
            public ChatResponse visit(PrivateChat c) {
                return mapPrivateChat(c);
            }

            @Override
            public ChatResponse visit(GroupChat c) {
                return mapGroupChat(c);
            }

            @Override
            public ChatResponse visit(Channel c) {
                return mapChannel(c);
            }

            @Override
            public ChatResponse visit(SavedMessages c) {
                return mapSavedMessages(c);
            }
        });
    }

    public List<ChatResponse> toResponseList(List<Chat> chats) {
        if (chats == null) {
            return null;
        }
        return chats.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Mapping(target = "id", source = "id.value")
    @Mapping(target = "type", constant = "PRIVATE")
    @Mapping(target = "isDeletable", source = "deletable")
    @Mapping(target = "partnerId", ignore = true)
    @Mapping(target = "partnerName", ignore = true)
    @Mapping(target = "partnerAvatarUrl", ignore = true)
    @Mapping(target = "partnerLastOnline", ignore = true)
    @Mapping(target = "lastMessage", ignore = true)
    @Mapping(target = "lastMessageAt", ignore = true)
    @Mapping(target = "unreadCount", ignore = true)
    @Mapping(target = "isPinned", ignore = true)
    protected abstract PrivateChatResponse mapPrivateChat(PrivateChat chat);

    @Mapping(target = "id", source = "id.value")
    @Mapping(target = "type", constant = "GROUP")
    @Mapping(target = "isDeletable", source = "deletable")
    @Mapping(target = "lastMessage", ignore = true)
    @Mapping(target = "lastMessageAt", ignore = true)
    @Mapping(target = "unreadCount", ignore = true)
    @Mapping(target = "isPinned", ignore = true)
    protected abstract GroupChatResponse mapGroupChat(GroupChat chat);

    @Mapping(target = "id", source = "id.value")
    @Mapping(target = "type", constant = "CHANNEL")
    @Mapping(target = "isDeletable", source = "deletable")
    @Mapping(target = "lastMessage", ignore = true)
    @Mapping(target = "lastMessageAt", ignore = true)
    @Mapping(target = "unreadCount", ignore = true)
    @Mapping(target = "isPinned", ignore = true)
    protected abstract ChannelResponse mapChannel(Channel chat);

    @Mapping(target = "id", source = "id.value")
    @Mapping(target = "type", constant = "SAVED_MESSAGES")
    @Mapping(target = "isDeletable", source = "deletable")
    @Mapping(target = "lastMessage", ignore = true)
    @Mapping(target = "lastMessageAt", ignore = true)
    @Mapping(target = "unreadCount", ignore = true)
    @Mapping(target = "isPinned", ignore = true)
    protected abstract SavedMessagesResponse mapSavedMessages(SavedMessages chat);

    @Mapping(target = "chatId", source = "chatId.value")
    @Mapping(target = "userId", source = "userId.value")
    @Mapping(target = "lastReadMessageId", source = "lastReadMessageId.value")
    @Mapping(target = "userDetails", ignore = true)
    @Mapping(target = "canSendMessages", expression = "java(chatMember.getPermissions().hasPermission(ChatMemberPermissions.PermissionType.SEND_MESSAGES))")
    @Mapping(target = "canDeleteMessages", expression = "java(chatMember.getPermissions().hasPermission(ChatMemberPermissions.PermissionType.DELETE_MESSAGES))")
    @Mapping(target = "canInviteUsers", expression = "java(chatMember.getPermissions().hasPermission(ChatMemberPermissions.PermissionType.INVITE_USERS))")
    @Mapping(target = "canChangeInfo", expression = "java(chatMember.getPermissions().hasPermission(ChatMemberPermissions.PermissionType.CHANGE_CHAT_INFO))")
    public abstract ChatMemberResponse toMemberResponse(ChatMember chatMember);

    public abstract List<ChatMemberResponse> toMemberResponseList(List<ChatMember> chatMembers);

    protected UUID map(ChatId value) {
        return value != null ? value.value() : null;
    }

    protected UUID map(UserId value) {
        return value != null ? value.value() : null;
    }
}