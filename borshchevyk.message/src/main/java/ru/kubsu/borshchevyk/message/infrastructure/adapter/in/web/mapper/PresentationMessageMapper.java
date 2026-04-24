package ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.kubsu.borshchevyk.message.domain.model.message.Message;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response.MessageResponse;

import java.util.List;

import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response.MessageReactionResponse;
import ru.kubsu.borshchevyk.message.domain.model.message.MessageReaction;

import ru.kubsu.borshchevyk.message.domain.model.message.MessageAttachment;

import org.springframework.beans.factory.annotation.Autowired;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response.EnrichedUserResponse;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.facade.UserEnrichmentService;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class PresentationMessageMapper {

    @Autowired
    private UserEnrichmentService userEnrichmentService;

    @Mapping(target = "id", source = "id.value")
    @Mapping(target = "chatId", source = "chatId.value")
    @Mapping(target = "authorId", source = "authorId.value")
    @Mapping(target = "authorName", ignore = true)
    @Mapping(target = "authorAvatarUrl", ignore = true)
    @Mapping(target = "isDeleted", source = "deleted")
    @Mapping(target = "pinnedBy", source = "pinnedBy.value")
    @Mapping(target = "forwardedFromChatId", source = "forwardedFromChatId.value")
    @Mapping(target = "forwardedFromUserId", source = "forwardedFromUserId.value")
    @Mapping(target = "parentMessageId", source = "parentMessageId.value")
    @Mapping(target = "commentsCount", source = "commentsCount")
    @Mapping(target = "updatedAt", source = "updatedAt")
    public abstract MessageResponse toBasicResponse(Message message);

    public MessageResponse toResponse(Message message) {
        MessageResponse response = toBasicResponse(message);
        if (response != null && response.authorId() != null) {
            EnrichedUserResponse user = userEnrichmentService.enrichUser(response.authorId());
            return enrich(response, user);
        }
        return response;
    }

    public List<MessageResponse> toResponseList(List<Message> messages) {
        if (messages == null) return null;
        
        List<MessageResponse> basicResponses = messages.stream()
                .map(this::toBasicResponse)
                .collect(Collectors.toList());
        
        List<UUID> authorIds = basicResponses.stream()
                .map(MessageResponse::authorId)
                .distinct()
                .toList();
        
        Map<UUID, EnrichedUserResponse> userMap = userEnrichmentService.enrichUsersToMap(authorIds);
        
        return basicResponses.stream()
                .map(r -> enrich(r, userMap.get(r.authorId())))
                .collect(Collectors.toList());
    }

    private MessageResponse enrich(MessageResponse r, EnrichedUserResponse user) {
        if (user == null) return r;
        return new MessageResponse(
                r.id(), r.chatId(), r.authorId(),
                (user.firstName() + " " + user.lastName()).trim(),
                user.avatarUrl(),
                r.text(), r.createdAt(), r.updatedAt(),
                r.isDeleted(), r.source(), r.status(),
                r.pinnedAt(), r.pinnedBy(),
                r.forwardedFromChatId(), r.forwardedFromUserId(),
                r.parentMessageId(), r.commentsCount(),
                r.attachments()
        );
    }

    public abstract MessageReactionResponse toReactionResponse(MessageReaction reaction);

    public abstract MessageResponse.AttachmentResponse toAttachmentResponse(MessageAttachment attachment);
}
