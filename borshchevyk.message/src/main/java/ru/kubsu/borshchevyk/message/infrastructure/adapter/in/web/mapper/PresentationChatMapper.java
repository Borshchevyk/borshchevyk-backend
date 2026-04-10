package ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.kubsu.borshchevyk.message.domain.model.chat.Chat;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response.ChatResponse;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PresentationChatMapper {
    @Mapping(target = "id", source = "id.value")
    ChatResponse toResponse(Chat chat);

    List<ChatResponse> toResponseList(List<Chat> chats);
}
