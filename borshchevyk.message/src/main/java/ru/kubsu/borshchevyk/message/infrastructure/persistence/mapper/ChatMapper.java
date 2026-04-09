package ru.kubsu.borshchevyk.message.infrastructure.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.kubsu.borshchevyk.message.domain.model.chat.Chat;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.entity.ChatEntity;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ChatMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "isDeleted", source = "deleted")
    ChatEntity toEntity(Chat domain);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "isDeleted", source = "deleted")
    Chat toDomain(ChatEntity entity);

    default UUID map(ChatId value) {
        return value != null ? value.value() : null;
    }

    default ChatId map(UUID value) {
        return value != null ? new ChatId(value) : null;
    }
}
