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

    @Autowired
    private ChatMemberRepository chatMemberRepository;

    @Autowired
    private RestTemplate restTemplate;

    public ChatResponse toResponse(Chat chat, @Context UUID requesterId) {
        if (chat == null) {
            return null;
        }

        if (chat instanceof PrivateChat p) {
            PrivateChatResponse response = PrivateChatResponse.builder()
                    .id(map(p.getId()))
                    .type(ChatType.PRIVATE)
                    .createdAt(p.getCreatedAt())
                    .build();

            // Find partner
            if (requesterId != null) {
                List<ChatMemberEntity> members = chatMemberRepository.findByChatId(p.getId().value());
                UUID partnerId = members.stream()
                        .map(ChatMemberEntity::getUserId)
                        .filter(id -> !id.equals(requesterId))
                        .findFirst()
                        .orElse(null);

                if (partnerId != null) {
                    response.setPartnerId(partnerId);
                    try {
                        ResponseEntity<JsonNode> userResponse = restTemplate.getForEntity(
                                "http://borshchevyk-user:8080/users/" + partnerId,
                                JsonNode.class
                        );
                        if (userResponse.getStatusCode().is2xxSuccessful() && userResponse.getBody() != null) {
                            JsonNode body = userResponse.getBody();
                            if (body.has("username")) response.setPartnerName(body.get("username").asText());
                            if (body.has("avatarUrl") && !body.get("avatarUrl").isNull()) response.setPartnerAvatarUrl(body.get("avatarUrl").asText());
                            // Parse lastOnline if exists
                        } else {
                            response.setPartnerName("Unknown User");
                        }
                    } catch (Exception e) {
                        response.setPartnerName("Unknown User");
                    }
                }
            }
            return response;
        } else if (chat instanceof GroupChat g) {
            return GroupChatResponse.builder()
                    .id(map(g.getId()))
                    .type(ChatType.GROUP)
                    .createdAt(g.getCreatedAt())
                    .title(g.getTitle())
                    .description(g.getDescription())
                    .commentsEnabled(g.isCommentsEnabled())
                    .build();
        } else if (chat instanceof Channel c) {
            return ChannelResponse.builder()
                    .id(map(c.getId()))
                    .type(ChatType.CHANNEL)
                    .createdAt(c.getCreatedAt())
                    .title(c.getTitle())
                    .description(c.getDescription())
                    .commentsEnabled(c.isCommentsEnabled())
                    .build();
        } else if (chat instanceof SavedMessages s) {
            return SavedMessagesResponse.builder()
                    .id(map(s.getId()))
                    .type(ChatType.SAVED_MESSAGES)
                    .createdAt(s.getCreatedAt())
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
