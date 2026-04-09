package ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.kubsu.borshchevyk.message.domain.model.message.Message;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response.MessageResponse;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PresentationMessageMapper {
    @Mapping(target = "id", source = "id.value")
    @Mapping(target = "chatId", source = "chatId.value")
    @Mapping(target = "authorId", source = "authorId.value")
    @Mapping(target = "isDeleted", source = "deleted")
    MessageResponse toResponse(Message message);

    List<MessageResponse> toResponseList(List<Message> messages);
}
