package ru.kubsu.borshchevyk.message.infrastructure.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.kubsu.borshchevyk.message.domain.model.message.Message;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.domain.model.value.MessageId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.entity.MessageEntity;

import java.util.UUID;

import ru.kubsu.borshchevyk.message.domain.model.message.MessageReaction;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.entity.MessageReactionEmbeddable;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    @Mapping(target = "id", source = "id.value")
    @Mapping(target = "chatId", source = "chatId.value")
    @Mapping(target = "authorId", source = "authorId.value")
    @Mapping(target = "isDeleted", source = "deleted")
    @Mapping(target = "pinnedBy", source = "pinnedBy.value")
    @Mapping(target = "forwardedFromChatId", source = "forwardedFromChatId.value")
    @Mapping(target = "forwardedFromUserId", source = "forwardedFromUserId.value")
    MessageEntity toEntity(Message domain);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "chatId", source = "chatId")
    @Mapping(target = "authorId", source = "authorId")
    @Mapping(target = "isDeleted", source = "deleted")
    @Mapping(target = "pinnedBy", source = "pinnedBy")
    @Mapping(target = "forwardedFromChatId", source = "forwardedFromChatId")
    @Mapping(target = "forwardedFromUserId", source = "forwardedFromUserId")
    Message toDomain(MessageEntity entity);

    MessageReactionEmbeddable toReactionEntity(MessageReaction reaction);
    MessageReaction toReactionDomain(MessageReactionEmbeddable entity);

    default UUID map(MessageId value) {
        return value != null ? value.value() : null;
    }

    default MessageId mapMessageId(UUID value) {
        return value != null ? new MessageId(value) : null;
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
}
