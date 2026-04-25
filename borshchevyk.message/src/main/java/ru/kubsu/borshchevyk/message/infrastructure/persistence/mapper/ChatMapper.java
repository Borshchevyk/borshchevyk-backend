package ru.kubsu.borshchevyk.message.infrastructure.persistence.mapper;

import org.mapstruct.Mapper;
import ru.kubsu.borshchevyk.message.domain.model.chat.*;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.entity.*;

import java.util.UUID;
import java.util.HashSet;

@Mapper(componentModel = "spring")
public interface ChatMapper {

    default ChatEntity toEntity(Chat domain) {
        if (domain == null) return null;
        if (domain instanceof PrivateChat p) {
            return PrivateChatEntity.builder()
                    .id(map(p.getId()))
                    .createdAt(p.getCreatedAt())
                    .isDeleted(p.isDeleted())
                    .isDeletable(p.isDeletable())
                    .allowedReactions(p.getAllowedReactions() != null ? new HashSet<>(p.getAllowedReactions()) : new HashSet<>())
                    .build();
        }
        if (domain instanceof SavedMessages p) {
            return SavedMessagesEntity.builder()
                    .id(map(p.getId()))
                    .createdAt(p.getCreatedAt())
                    .isDeleted(p.isDeleted())
                    .isDeletable(p.isDeletable())
                    .allowedReactions(p.getAllowedReactions() != null ? new HashSet<>(p.getAllowedReactions()) : new HashSet<>())
                    .build();
        }
        if (domain instanceof GroupChat p) {
            return GroupChatEntity.builder()
                    .id(map(p.getId()))
                    .createdAt(p.getCreatedAt())
                    .isDeleted(p.isDeleted())
                    .isDeletable(p.isDeletable())
                    .title(p.getTitle())
                    .description(p.getDescription())
                    .inviteCode(p.getInviteCode())
                    .commentsEnabled(p.isCommentsEnabled())
                    .allowedReactions(p.getAllowedReactions() != null ? new HashSet<>(p.getAllowedReactions()) : new HashSet<>())
                    .build();
        }
        if (domain instanceof Channel p) {
            return ChannelEntity.builder()
                    .id(map(p.getId()))
                    .createdAt(p.getCreatedAt())
                    .isDeleted(p.isDeleted())
                    .isDeletable(p.isDeletable())
                    .title(p.getTitle())
                    .description(p.getDescription())
                    .inviteCode(p.getInviteCode())
                    .commentsEnabled(p.isCommentsEnabled())
                    .allowedReactions(p.getAllowedReactions() != null ? new HashSet<>(p.getAllowedReactions()) : new HashSet<>())
                    .build();
        }
        throw new IllegalArgumentException("Unknown chat type: " + domain.getClass());
    }

    default Chat toDomain(ChatEntity entity) {
        if (entity == null) return null;
        if (entity instanceof PrivateChatEntity p) {
            return PrivateChat.builder()
                    .id(map(p.getId()))
                    .createdAt(p.getCreatedAt())
                    .isDeleted(p.isDeleted())
                    .isDeletable(p.isDeletable())
                    .type(ChatType.PRIVATE)
                    .allowedReactions(p.getAllowedReactions() != null ? new HashSet<>(p.getAllowedReactions()) : new HashSet<>())
                    .build();
        }
        if (entity instanceof SavedMessagesEntity p) {
            return SavedMessages.builder()
                    .id(map(p.getId()))
                    .createdAt(p.getCreatedAt())
                    .isDeleted(p.isDeleted())
                    .isDeletable(p.isDeletable())
                    .type(ChatType.SAVED_MESSAGES)
                    .allowedReactions(p.getAllowedReactions() != null ? new HashSet<>(p.getAllowedReactions()) : new HashSet<>())
                    .build();
        }
        if (entity instanceof GroupChatEntity p) {
            return GroupChat.builder()
                    .id(map(p.getId()))
                    .createdAt(p.getCreatedAt())
                    .isDeleted(p.isDeleted())
                    .isDeletable(p.isDeletable())
                    .title(p.getTitle())
                    .description(p.getDescription())
                    .inviteCode(p.getInviteCode())
                    .commentsEnabled(p.isCommentsEnabled())
                    .type(ChatType.GROUP)
                    .allowedReactions(p.getAllowedReactions() != null ? new HashSet<>(p.getAllowedReactions()) : new HashSet<>())
                    .build();
        }
        if (entity instanceof ChannelEntity p) {
            return Channel.builder()
                    .id(map(p.getId()))
                    .createdAt(p.getCreatedAt())
                    .isDeleted(p.isDeleted())
                    .isDeletable(p.isDeletable())
                    .title(p.getTitle())
                    .description(p.getDescription())
                    .inviteCode(p.getInviteCode())
                    .commentsEnabled(p.isCommentsEnabled())
                    .type(ChatType.CHANNEL)
                    .allowedReactions(p.getAllowedReactions() != null ? new HashSet<>(p.getAllowedReactions()) : new HashSet<>())
                    .build();
        }
        throw new IllegalArgumentException("Unknown chat entity type: " + entity.getClass());
    }

    default UUID map(ChatId value) {
        return value != null ? value.value() : null;
    }

    default ChatId map(UUID value) {
        return value != null ? new ChatId(value) : null;
    }
}
