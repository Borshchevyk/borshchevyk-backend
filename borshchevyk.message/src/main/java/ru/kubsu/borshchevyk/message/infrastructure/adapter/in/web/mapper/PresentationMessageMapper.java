package ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.kubsu.borshchevyk.message.domain.model.message.Message;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response.MessageResponse;

import java.util.List;

import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response.MessageReactionResponse;
import ru.kubsu.borshchevyk.message.domain.model.message.MessageReaction;

@Mapper(componentModel = "spring")
public interface PresentationMessageMapper {
    @Mapping(target = "id", source = "id.value")
    @Mapping(target = "chatId", source = "chatId.value")
    @Mapping(target = "authorId", source = "authorId.value")
    @Mapping(target = "isDeleted", source = "deleted")
    @Mapping(target = "pinnedBy", source = "pinnedBy.value")
    @Mapping(target = "forwardedFromChatId", source = "forwardedFromChatId.value")
    @Mapping(target = "forwardedFromUserId", source = "forwardedFromUserId.value")
    @Mapping(target = "parentMessageId", source = "parentMessageId.value")
    @Mapping(target = "commentsCount", source = "commentsCount")
    MessageResponse toResponse(Message message);

    List<MessageResponse> toResponseList(List<Message> messages);

    MessageReactionResponse toReactionResponse(MessageReaction reaction);
}
