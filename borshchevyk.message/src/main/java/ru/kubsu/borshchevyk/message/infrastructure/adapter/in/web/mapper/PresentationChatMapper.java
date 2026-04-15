package ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.kubsu.borshchevyk.message.domain.model.chat.*;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response.*;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.entity.ChatMemberEntity;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.repository.ChatMemberRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class PresentationChatMapper {

    public ChatResponse toResponse(Chat chat, @Context UUID requesterId) {
        if (chat == null) {
            return null;
        }

        if (chat instanceof PrivateChat p) {
            PrivateChatResponse response = PrivateChatResponse.builder()
                    .id(map(p.getId()))
                    .type(ChatType.PRIVATE)
                    .createdAt(p.getCreatedAt())
                    .allowedReactions(p.getAllowedReactions())
                    .build();
            return response;
        } else if (chat instanceof GroupChat g) {
            return GroupChatResponse.builder()
                    .id(map(g.getId()))
                    .type(ChatType.GROUP)
                    .createdAt(g.getCreatedAt())
                    .title(g.getTitle())
                    .description(g.getDescription())
                    .commentsEnabled(g.isCommentsEnabled())
                    .allowedReactions(g.getAllowedReactions())
                    .build();
        } else if (chat instanceof Channel c) {
            return ChannelResponse.builder()
                    .id(map(c.getId()))
                    .type(ChatType.CHANNEL)
                    .createdAt(c.getCreatedAt())
                    .title(c.getTitle())
                    .description(c.getDescription())
                    .commentsEnabled(c.isCommentsEnabled())
                    .allowedReactions(c.getAllowedReactions())
                    .build();
        } else if (chat instanceof SavedMessages s) {
            return SavedMessagesResponse.builder()
                    .id(map(s.getId()))
                    .type(ChatType.SAVED_MESSAGES)
                    .createdAt(s.getCreatedAt())
                    .allowedReactions(s.getAllowedReactions())
                    .build();
        }

        throw new IllegalArgumentException("Unknown chat type");
    }

    public List<ChatResponse> toResponseList(List<Chat> chats, @Context UUID requesterId) {
        if (chats == null) {
            return null;
        }
        return chats.stream()
                .map(chat -> toResponse(chat, requesterId))
                .collect(Collectors.toList());
    }

    @Mapping(target = "chatId", source = "chatId.value")
    @Mapping(target = "userId", source = "userId.value")
    @Mapping(target = "lastReadMessageId", source = "lastReadMessageId.value")
    public abstract ChatMemberResponse toMemberResponse(ChatMember chatMember);

    public abstract List<ChatMemberResponse> toMemberResponseList(List<ChatMember> chatMembers);

    protected UUID map(ChatId value) {
        return value != null ? value.value() : null;
    }

    protected UUID map(UserId value) {
        return value != null ? value.value() : null;
    }
}
