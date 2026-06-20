package ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.kubsu.borshchevyk.message.infrastructure.websocket.WebSocketEventBroadcaster;
import org.springframework.web.bind.annotation.*;
import ru.kubsu.borshchevyk.message.application.dto.command.JoinChatByLinkCommand;
import ru.kubsu.borshchevyk.message.application.dto.query.GenerateInviteLinkQuery;
import ru.kubsu.borshchevyk.message.application.port.in.GenerateInviteLinkUseCase;
import ru.kubsu.borshchevyk.message.application.port.in.JoinChatByLinkUseCase;
import ru.kubsu.borshchevyk.message.application.port.out.PublishChatEventPort;
import ru.kubsu.borshchevyk.message.domain.event.chat.ChatMemberEvent;
import ru.kubsu.borshchevyk.message.domain.model.chat.Chat;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response.ChatResponse;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.facade.ChatEnrichmentService;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.facade.ChatFacade;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.facade.UserEnrichmentService;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/chats")
@RequiredArgsConstructor
@Tag(name = "Chat Invites", description = "Endpoints for generating and joining by invite links")
public class ChatInviteController {

    private final GenerateInviteLinkUseCase generateInviteLinkUseCase;
    private final JoinChatByLinkUseCase joinChatByLinkUseCase;
    private final ChatFacade chatFacade;
    private final WebSocketEventBroadcaster eventBroadcaster;
    private final PublishChatEventPort PublishChatEventPort;
    private final ChatEnrichmentService chatEnrichmentService;
    private final UserEnrichmentService userEnrichmentService;

    @Operation(summary = "Generate invite link", description = "Generates a new invite link for the chat.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Link generated successfully")
    })
    @PostMapping("/{chatId}/invite-link")
    public String generateInviteLink(
            @PathVariable UUID chatId,
            @RequestHeader("X-User-Id") UUID requesterId) {
        log.info("Request to generate invite link for chat {} from user {}", chatId, requesterId);
        GenerateInviteLinkQuery query = new GenerateInviteLinkQuery(chatId, requesterId);
        return generateInviteLinkUseCase.generateInviteLink(query);
    }

    @Operation(summary = "Join chat by link", description = "Joins a chat using an invite code.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Joined successfully")
    })
    @PostMapping("/join/{inviteCode}")
    public ChatResponse joinChatByLink(
            @PathVariable String inviteCode,
            @RequestHeader("X-User-Id") UUID userId) {
        log.info("Request to join chat by link from user {}", userId);
        JoinChatByLinkCommand command = new JoinChatByLinkCommand(inviteCode, userId);
        Chat chat = joinChatByLinkUseCase.joinChatByLink(command);
        eventBroadcaster.broadcastToChatMembers(chat.getId().value(), "MEMBER_ADDED",
                new ChatMemberEvent(chatEnrichmentService.enrichChat(chat.getId().value(), userId), userEnrichmentService.enrichUser(userId), "JOIN"));
        
        PublishChatEventPort.publishChatEvent(
                new UserId(userId), 
                chat.getId(), 
                "JOINED"
        );
        return chatFacade.enrichChatResponse(chat, userId);
    }
}