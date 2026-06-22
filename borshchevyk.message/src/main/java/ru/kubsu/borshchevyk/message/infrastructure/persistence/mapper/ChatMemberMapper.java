package ru.kubsu.borshchevyk.message.infrastructure.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatMember;
import ru.kubsu.borshchevyk.message.domain.model.chat.member_permissions.ChatMemberPermissions;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.domain.model.value.MessageId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.entity.ChatMemberEntity;

import java.util.UUID;

@Mapper(componentModel = "spring", imports = {ChatMemberPermissions.class})
public interface ChatMemberMapper {

    @Mapping(target = "chatId", source = "chatId")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "lastReadMessageId", source = "lastReadMessageId.value")
    @Mapping(target = "lastReadAt", source = "lastReadAt")
    @Mapping(target = "isPinned", source = "pinned")
    @Mapping(target = "canSendMessages", expression = "java(domain.getPermissions().hasPermission(ChatMemberPermissions.PermissionType.SEND_MESSAGES))")
    @Mapping(target = "canDeleteMessages", expression = "java(domain.getPermissions().hasPermission(ChatMemberPermissions.PermissionType.DELETE_MESSAGES))")
    @Mapping(target = "canInviteUsers", expression = "java(domain.getPermissions().hasPermission(ChatMemberPermissions.PermissionType.INVITE_USERS))")
    @Mapping(target = "canChangeInfo", expression = "java(domain.getPermissions().hasPermission(ChatMemberPermissions.PermissionType.CHANGE_CHAT_INFO))")
    ChatMemberEntity toEntity(ChatMember domain);

    @Mapping(target = "chatId", source = "chatId")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "lastReadMessageId", source = "lastReadMessageId")
    @Mapping(target = "lastReadAt", source = "lastReadAt")
    @Mapping(target = "isPinned", source = "pinned")
    @Mapping(target = "permissions", source = "entity")
    ChatMember toDomain(ChatMemberEntity entity);

    default ChatMemberPermissions mapPermissions(ChatMemberEntity entity) {
        ChatMemberPermissions permissions = new ChatMemberPermissions();
        if (!entity.isCanSendMessages()) permissions.revokePermission(ChatMemberPermissions.PermissionType.SEND_MESSAGES);
        if (!entity.isCanDeleteMessages()) permissions.revokePermission(ChatMemberPermissions.PermissionType.DELETE_MESSAGES);
        if (!entity.isCanInviteUsers()) permissions.revokePermission(ChatMemberPermissions.PermissionType.INVITE_USERS);
        if (!entity.isCanChangeInfo()) permissions.revokePermission(ChatMemberPermissions.PermissionType.CHANGE_CHAT_INFO);
        return permissions;
    }

    default UUID map(ChatId value) {
        return value != null ? value.value() : null;
    }

    default ChatId mapChatId(UUID value) {
        return value != null ? new ChatId(value) : null;
    }

    default UUID map(UserId value) {
        return value != null ? value.value() : null;
    }

    default UserId mapUserId(UUID value) {
        return value != null ? new UserId(value) : null;
    }

    default UUID map(MessageId value) {
        return value != null ? value.value() : null;
    }

    default MessageId mapMessageId(UUID value) {
        return value != null ? new MessageId(value) : null;
    }
}