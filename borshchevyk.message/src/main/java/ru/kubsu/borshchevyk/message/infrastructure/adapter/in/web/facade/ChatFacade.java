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
    private final ru.kubsu.borshchevyk.message.infrastructure.adapter.out.grpc.UserGrpcClient userGrpcClient;

    public ChatResponse enrichChatResponse(Chat chat, UUID requesterId) {
        ChatResponse response = presentationChatMapper.toResponse(chat, requesterId);
        if (response instanceof PrivateChatResponse pResponse) {
            enrichPrivateChatResponses(List.of(pResponse), requesterId);
        }
        return response;
    }

    public List<ChatResponse> enrichChatResponses(List<Chat> chats, UUID requesterId) {
        List<ChatResponse> responses = presentationChatMapper.toResponseList(chats, requesterId);
        
        List<PrivateChatResponse> privateChatResponses = responses.stream()
                .filter(r -> r instanceof PrivateChatResponse)
                .map(r -> (PrivateChatResponse) r)
                .collect(Collectors.toList());

        enrichPrivateChatResponses(privateChatResponses, requesterId);
        return responses;
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

        Map<UUID, ru.kubsu.borshchevyk.grpc.UserResponse> userProfileMap = new HashMap<>();
        try {
            List<ru.kubsu.borshchevyk.grpc.UserResponse> userResponses = userGrpcClient.getUsersBatch(new ArrayList<>(partnerIds));
            for (ru.kubsu.borshchevyk.grpc.UserResponse u : userResponses) {
                userProfileMap.put(UUID.fromString(u.getUserId()), u);
            }
        } catch (Exception e) {
            log.error("Failed to fetch user profiles batch via gRPC", e);
        }

        for (PrivateChatResponse response : privateChats) {
            UUID partnerId = chatPartnerMap.get(response.getId());
            if (partnerId != null) {
                response.setPartnerId(partnerId);
                ru.kubsu.borshchevyk.grpc.UserResponse userProfile = userProfileMap.get(partnerId);
                if (userProfile != null) {
                    String name = (userProfile.getFirstName() + " " + userProfile.getLastName()).trim();
                    if (name.isEmpty()) name = userProfile.getTag();
                    response.setPartnerName(name);
                    if (!userProfile.getAvatarUrl().isEmpty()) {
                        response.setPartnerAvatarUrl(userProfile.getAvatarUrl());
                    }
                } else {
                    response.setPartnerName("Unknown User");
                }
            }
        }
    }
}
