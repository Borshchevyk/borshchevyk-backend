package ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.kubsu.borshchevyk.message.domain.model.message.Message;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response.MessageResponse;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response.MessageReactionResponse;
import ru.kubsu.borshchevyk.message.domain.model.message.MessageReaction;
import ru.kubsu.borshchevyk.message.domain.model.message.MessageAttachment;
import org.springframework.beans.factory.annotation.Autowired;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response.ShortUserDto;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response.ShortChatDto;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.facade.UserEnrichmentService;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.facade.ChatEnrichmentService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class PresentationMessageMapper {

    @Autowired
    private UserEnrichmentService userEnrichmentService;

    @Autowired
    private ChatEnrichmentService chatEnrichmentService;

    @Mapping(target = "id", source = "id.value")
    @Mapping(target = "chat", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "isDeleted", source = "deleted")
    @Mapping(target = "pinnedBy", source = "pinnedBy.value")
    @Mapping(target = "forwardedFromChat", ignore = true)
    @Mapping(target = "forwardedFromUser", ignore = true)
    @Mapping(target = "parentMessageId", source = "parentMessageId.value")
    @Mapping(target = "commentsCount", source = "commentsCount")
    @Mapping(target = "updatedAt", source = "updatedAt")
    public abstract MessageResponse toBasicResponse(Message message);

    public MessageResponse toResponse(Message message, @Context UUID currentUserId) {
        MessageResponse response = toBasicResponse(message);
        if (response != null) {
            ShortUserDto author = message.getAuthorId() != null ? userEnrichmentService.enrichUser(message.getAuthorId().value()) : null;
            ShortChatDto chat = message.getChatId() != null ? chatEnrichmentService.enrichChat(message.getChatId().value(), currentUserId) : null;
            ShortChatDto fwChat = message.getForwardedFromChatId() != null ? chatEnrichmentService.enrichChat(message.getForwardedFromChatId().value(), currentUserId) : null;
            ShortUserDto fwUser = message.getForwardedFromUserId() != null ? userEnrichmentService.enrichUser(message.getForwardedFromUserId().value()) : null;
            return enrich(response, author, chat, fwChat, fwUser);
        }
        return response;
    }

    public List<MessageResponse> toResponseList(List<Message> messages, @Context UUID currentUserId) {
        if (messages == null) return null;
        
        return messages.stream().map(msg -> {
            ShortUserDto author = msg.getAuthorId() != null ? userEnrichmentService.enrichUser(msg.getAuthorId().value()) : null;
            ShortChatDto chat = msg.getChatId() != null ? chatEnrichmentService.enrichChat(msg.getChatId().value(), currentUserId) : null;
            ShortChatDto fwChat = msg.getForwardedFromChatId() != null ? chatEnrichmentService.enrichChat(msg.getForwardedFromChatId().value(), currentUserId) : null;
            ShortUserDto fwUser = msg.getForwardedFromUserId() != null ? userEnrichmentService.enrichUser(msg.getForwardedFromUserId().value()) : null;
            MessageResponse basic = toBasicResponse(msg);
            return enrich(basic, author, chat, fwChat, fwUser);
        }).collect(Collectors.toList());
    }

    private MessageResponse enrich(MessageResponse r, ShortUserDto author, ShortChatDto chat, ShortChatDto fwChat, ShortUserDto fwUser) {
        return new MessageResponse(
                r.id(), chat, author,
                r.text(), r.createdAt(), r.updatedAt(),
                r.isDeleted(), r.source(), r.status(),
                r.pinnedAt(), r.pinnedBy(),
                fwChat, fwUser,
                r.parentMessageId(), r.commentsCount(),
                r.attachments()
        );
    }

    public abstract MessageReactionResponse toReactionResponse(MessageReaction reaction);

    public abstract MessageResponse.AttachmentResponse toAttachmentResponse(MessageAttachment attachment);
}
