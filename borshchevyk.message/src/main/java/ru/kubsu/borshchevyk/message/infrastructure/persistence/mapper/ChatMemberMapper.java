package ru.kubsu.borshchevyk.message.infrastructure.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatMember;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;
import ru.kubsu.borshchevyk.message.domain.model.value.MessageId;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.entity.ChatMemberEntity;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ChatMemberMapper {

    @Mapping(target = "chatId", source = "chatId")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "lastReadMessageId", source = "lastReadMessageId.value")
    @Mapping(target = "isPinned", source = "pinned")
    ChatMemberEntity toEntity(ChatMember domain);

    @Mapping(target = "chatId", source = "chatId")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "lastReadMessageId", source = "lastReadMessageId")
    @Mapping(target = "isPinned", source = "pinned")
    ChatMember toDomain(ChatMemberEntity entity);

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
