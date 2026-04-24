package ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
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
    private ru.kubsu.borshchevyk.message.infrastructure.adapter.out.grpc.UserGrpcClient userGrpcClient;

    public ChatResponse toResponse(Chat chat, @Context UUID requesterId) {
        if (chat == null) {
            return null;
        }

        if (chat instanceof PrivateChat p) {
            UUID partnerId = chatMemberRepository.findByChatId(p.getId().value())
                    .stream()
                    .map(ChatMemberEntity::getUserId)
                    .filter(id -> !id.equals(requesterId))
                    .findFirst()
                    .orElse(null);

            String partnerName = null;
            String partnerAvatarUrl = null;
            LocalDateTime partnerLastOnline = null;

            if (partnerId != null) {
                try {
                    ru.kubsu.borshchevyk.grpc.UserResponse userResponse = userGrpcClient.getUserInfo(partnerId);
                    partnerName = (userResponse.getFirstName() + " " + userResponse.getLastName()).trim();
                    if (partnerName.isEmpty()) {
                        partnerName = userResponse.getTag();
                    }
                    partnerAvatarUrl = userResponse.getAvatarUrl().isEmpty() ? null : userResponse.getAvatarUrl();
                } catch (Exception e) {
                    // Fallback to ID if user-service is down
                    partnerName = "User " + partnerId.toString().substring(0, 8);
                }
            }

            return PrivateChatResponse.builder()
                    .id(map(p.getId()))
                    .type(ChatType.PRIVATE)
                    .createdAt(p.getCreatedAt())
                    .allowedReactions(p.getAllowedReactions())
                    .partnerId(partnerId)
                    .partnerName(partnerName)
                    .partnerAvatarUrl(partnerAvatarUrl)
                    .partnerLastOnline(partnerLastOnline)
                    .build();
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
