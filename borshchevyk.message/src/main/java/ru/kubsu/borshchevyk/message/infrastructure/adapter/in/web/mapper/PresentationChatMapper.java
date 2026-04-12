package ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.kubsu.borshchevyk.message.domain.model.chat.Chat;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatMember;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response.ChatResponse;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response.ChatMemberResponse;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface PresentationChatMapper {

    @Mapping(target = "id", source = "id.value")
    ChatResponse toResponse(Chat chat);

    List<ChatResponse> toResponseList(List<Chat> chats);

    @Mapping(target = "chatId", source = "chatId.value")
    @Mapping(target = "userId", source = "userId.value")
    @Mapping(target = "lastReadMessageId", source = "lastReadMessageId.value")
    ChatMemberResponse toMemberResponse(ChatMember chatMember);

    List<ChatMemberResponse> toMemberResponseList(List<ChatMember> chatMembers);

    default UUID map(ChatId value) {
        return value != null ? value.value() : null;
    }
    
    default UUID map(UserId value) {
        return value != null ? value.value() : null;
    }
}
