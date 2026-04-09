package ru.kubsu.borshchevyk.message.infrastructure.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.kubsu.borshchevyk.message.domain.model.message.Message;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.domain.model.value.MessageId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.entity.MessageEntity;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "chatId", source = "chatId")
    @Mapping(target = "authorId", source = "authorId")
    @Mapping(target = "isDeleted", source = "deleted")
    MessageEntity toEntity(Message domain);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "chatId", source = "chatId")
    @Mapping(target = "authorId", source = "authorId")
    @Mapping(target = "isDeleted", source = "deleted")
    Message toDomain(MessageEntity entity);

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
