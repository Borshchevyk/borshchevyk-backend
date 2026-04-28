package ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.facade;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.kubsu.borshchevyk.message.domain.model.chat.Chat;
import ru.kubsu.borshchevyk.message.domain.model.chat.PrivateChat;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response.ChatResponse;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response.PrivateChatResponse;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.mapper.PresentationChatMapper;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.entity.ChatMemberEntity;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.repository.ChatMemberRepository;

import java.util.*;
import java.util.stream.Collectors;
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatFacade {

    private final PresentationChatMapper presentationChatMapper;
    private final ChatMemberRepository chatMemberRepository;
    private final ru.kubsu.borshchevyk.message.application.port.out.ChatMemberPort chatMemberPort;
    private final ru.kubsu.borshchevyk.message.application.port.out.MessagePort messagePort;
    private final ru.kubsu.borshchevyk.message.infrastructure.adapter.out.grpc.UserGrpcClient userGrpcClient;

    private final ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.facade.UserEnrichmentService userEnrichmentService;

    public ChatResponse enrichChatResponse(Chat chat, UUID requesterId) {
        ChatResponse response = presentationChatMapper.toResponse(chat, requesterId);
        enrichWithLastMessageAndUnreadCount(List.of(response), requesterId);
        if (response instanceof PrivateChatResponse pResponse) {
            enrichPrivateChatResponses(List.of(pResponse), requesterId);
        }
        return response;
    }

    public List<ChatResponse> enrichChatResponses(List<Chat> chats, UUID requesterId) {
        List<ChatResponse> responses = presentationChatMapper.toResponseList(chats, requesterId);
        
        enrichWithLastMessageAndUnreadCount(responses, requesterId);

        List<PrivateChatResponse> privateChatResponses = responses.stream()
                .filter(r -> r instanceof PrivateChatResponse)
                .map(r -> (PrivateChatResponse) r)
                .collect(Collectors.toList());

        enrichPrivateChatResponses(privateChatResponses, requesterId);

        responses.sort((r1, r2) -> {
            if (r1.isPinned() && !r2.isPinned()) return -1;
            if (!r1.isPinned() && r2.isPinned()) return 1;

            java.time.LocalDateTime t1 = r1.getLastMessageAt() != null ? r1.getLastMessageAt() : r1.getCreatedAt();
            java.time.LocalDateTime t2 = r2.getLastMessageAt() != null ? r2.getLastMessageAt() : r2.getCreatedAt();

            if (t1 == null && t2 == null) return 0;
            if (t1 == null) return 1;
            if (t2 == null) return -1;

            return t2.compareTo(t1);
        });

        return responses;
    }

    private void enrichWithLastMessageAndUnreadCount(List<ChatResponse> responses, UUID requesterId) {
        if (requesterId == null) return;
        ru.kubsu.borshchevyk.message.domain.model.value.UserId userId = new ru.kubsu.borshchevyk.message.domain.model.value.UserId(requesterId);

        for (ChatResponse response : responses) {
            ru.kubsu.borshchevyk.message.domain.model.value.ChatId chatId = new ru.kubsu.borshchevyk.message.domain.model.value.ChatId(response.getId());
            
            chatMemberPort.findByChatIdAndUserId(chatId, userId).ifPresent(member -> {
                java.time.LocalDateTime historyClearedAt = member.getHistoryClearedAt();
                
                // Last message
                messagePort.getLastMessage(chatId, userId, historyClearedAt).ifPresent(msg -> {
                    response.setLastMessage(msg.getText());
                    response.setLastMessageAt(msg.getCreatedAt());
                });

                // Unread count
                java.time.LocalDateTime lastReadAt = member.getLastReadAt();
                
                long unread = messagePort.countUnreadMessages(chatId, userId, historyClearedAt, lastReadAt);
                response.setUnreadCount(unread);
                response.setPinned(member.isPinned());
            });
        }
    }

    private void enrichPrivateChatResponses(List<PrivateChatResponse> privateChats, UUID requesterId) {
        if (privateChats.isEmpty() || requesterId == null) {
            return;
        }

        List<UUID> chatIds = privateChats.stream()
                .map(ChatResponse::getId)
                .collect(Collectors.toList());

        List<ChatMemberEntity> members = chatMemberRepository.findByChatIdIn(chatIds);

        Map<UUID, UUID> chatPartnerMap = new HashMap<>();
        Set<UUID> partnerIds = new HashSet<>();

        for (ChatMemberEntity member : members) {
            if (!member.getUserId().equals(requesterId)) {
                chatPartnerMap.put(member.getChatId(), member.getUserId());
                partnerIds.add(member.getUserId());
            }
        }

        if (partnerIds.isEmpty()) {
            return;
        }

        Map<UUID, ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response.ShortUserDto> userProfileMap = userEnrichmentService.enrichUsersToMap(new ArrayList<>(partnerIds));

        for (PrivateChatResponse response : privateChats) {
            UUID partnerId = chatPartnerMap.get(response.getId());
            if (partnerId != null) {
                response.setPartnerId(partnerId);
                ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response.ShortUserDto userProfile = userProfileMap.get(partnerId);
                if (userProfile != null) {
                    String name = (userProfile.firstName() + " " + userProfile.lastName()).trim();
                    if (name.isEmpty()) name = userProfile.tag();
                    response.setPartnerName(name);
                    if (userProfile.avatarUrl() != null && !userProfile.avatarUrl().isEmpty()) {
                        response.setPartnerAvatarUrl(userProfile.avatarUrl());
                    }
                } else {
                    response.setPartnerName("Unknown User");
                }
            }
        }
    }
}
