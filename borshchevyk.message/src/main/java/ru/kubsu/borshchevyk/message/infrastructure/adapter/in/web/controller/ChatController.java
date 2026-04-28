package ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.kubsu.borshchevyk.message.application.dto.command.CreateChatCommand;
import ru.kubsu.borshchevyk.message.application.port.in.ClearChatHistoryUseCase;
import ru.kubsu.borshchevyk.message.application.port.in.CreateChatUseCase;
import ru.kubsu.borshchevyk.message.application.port.in.DeleteChatUseCase;
import ru.kubsu.borshchevyk.message.application.port.in.LoadUserChatsUseCase;
import ru.kubsu.borshchevyk.message.application.port.in.GenerateInviteLinkUseCase;
import ru.kubsu.borshchevyk.message.application.port.in.InviteUserUseCase;
import ru.kubsu.borshchevyk.message.application.port.in.JoinChatByLinkUseCase;
import ru.kubsu.borshchevyk.message.application.port.in.KickUserUseCase;
import ru.kubsu.borshchevyk.message.application.port.in.LeaveChatUseCase;
import ru.kubsu.borshchevyk.message.application.dto.command.InviteUserCommand;
import ru.kubsu.borshchevyk.message.application.dto.command.KickUserCommand;
import ru.kubsu.borshchevyk.message.application.dto.command.LeaveChatCommand;
import ru.kubsu.borshchevyk.message.domain.model.chat.Chat;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.request.CreateChatRequest;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response.ChatResponse;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.mapper.PresentationChatMapper;
import ru.kubsu.borshchevyk.message.infrastructure.exception.MessageErrorResponse;

import java.util.List;
import java.util.UUID;

import ru.kubsu.borshchevyk.message.application.port.in.LoadChatMembersUseCase;
import ru.kubsu.borshchevyk.message.application.port.in.UpdateChatInfoUseCase;
import ru.kubsu.borshchevyk.message.application.dto.command.UpdateChatInfoCommand;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.request.UpdateChatInfoRequest;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response.ChatMemberResponse;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatMember;

import ru.kubsu.borshchevyk.message.infrastructure.websocket.dto.ChatMemberEvent;
import ru.kubsu.borshchevyk.message.infrastructure.websocket.dto.ChatInfoEvent;
import ru.kubsu.borshchevyk.message.infrastructure.websocket.dto.ChatSettingsEvent;
import org.springframework.messaging.simp.SimpMessagingTemplate;

/**
 * Controller for managing chats, their settings, and members.
 * Handles HTTP requests and performs real-time notifications via WebSockets and external services.
 *
 * @author Aleksey Timko
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/chats")
@RequiredArgsConstructor
@Tag(name = "Chat", description = "Endpoints for managing chats")
public class ChatController {

    private final CreateChatUseCase createChatUseCase;
    private final ru.kubsu.borshchevyk.message.application.port.in.UpdateMemberPermissionsUseCase updateMemberPermissionsUseCase;
    private final ClearChatHistoryUseCase clearChatHistoryUseCase;
    private final DeleteChatUseCase deleteChatUseCase;
    private final LoadUserChatsUseCase loadUserChatsUseCase;
    private final InviteUserUseCase inviteUserUseCase;
    private final KickUserUseCase kickUserUseCase;
    private final LeaveChatUseCase leaveChatUseCase;
    private final GenerateInviteLinkUseCase generateInviteLinkUseCase;
    private final JoinChatByLinkUseCase joinChatByLinkUseCase;
    private final UpdateChatInfoUseCase updateChatInfoUseCase;
    private final LoadChatMembersUseCase loadChatMembersUseCase;
    private final ru.kubsu.borshchevyk.message.application.port.in.UpdateChatReactionsUseCase updateChatReactionsUseCase;
    private final ru.kubsu.borshchevyk.message.application.port.in.SearchChatsUseCase searchChatsUseCase;
    private final PresentationChatMapper presentationChatMapper;
    private final ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.facade.ChatFacade chatFacade;
    private final SimpMessagingTemplate messagingTemplate;

    private final ru.kubsu.borshchevyk.message.application.port.out.RealtimeNotificationPort realtimeNotificationPort;

    private final ru.kubsu.borshchevyk.message.application.port.in.PinChatUseCase pinChatUseCase;
    private final ru.kubsu.borshchevyk.message.application.port.in.UnpinChatUseCase unpinChatUseCase;

    @Operation(summary = "Pin a chat", description = "Pins the chat for the requester")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Chat pinned successfully")
    })
    @PostMapping("/{chatId}/pin")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void pinChat(
            @PathVariable UUID chatId,
            @RequestHeader("X-User-Id") UUID userId) {
        log.info("Request to pin chat {} from user: {}", chatId, userId);
        ru.kubsu.borshchevyk.message.application.dto.command.PinChatCommand command = ru.kubsu.borshchevyk.message.application.dto.command.PinChatCommand.builder()
                .chatId(chatId)
                .requesterId(userId)
                .build();
        pinChatUseCase.pinChat(command);
    }

    @Operation(summary = "Unpin a chat", description = "Unpins the chat for the requester")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Chat unpinned successfully")
    })
    @DeleteMapping("/{chatId}/pin")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unpinChat(
            @PathVariable UUID chatId,
            @RequestHeader("X-User-Id") UUID userId) {
        log.info("Request to unpin chat {} from user: {}", chatId, userId);
        ru.kubsu.borshchevyk.message.application.dto.command.UnpinChatCommand command = ru.kubsu.borshchevyk.message.application.dto.command.UnpinChatCommand.builder()
                .chatId(chatId)
                .requesterId(userId)
                .build();
        unpinChatUseCase.unpinChat(command);
    }

    @Operation(summary = "Create a new chat", description = "Creates a new chat with the given type, title, description, and initial members.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chat created successfully",
                    content = @Content(schema = @Schema(implementation = ChatResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request format",
                    content = @Content(schema = @Schema(implementation = MessageErrorResponse.class)))
    })
    @PostMapping
    public ChatResponse createChat(
            @RequestHeader("X-User-Id") @Parameter(description = "ID of the authenticated user") UUID userId,
            @RequestBody CreateChatRequest request) {
        log.info("Request to create chat from user: {}", userId);
        CreateChatCommand command = CreateChatCommand.builder()
                .creatorId(userId)
                .type(request.type())
                .title(request.title())
                .description(request.description())
                .initialMemberIds(request.initialMemberIds())
                .commentsEnabled(request.commentsEnabled() != null ? request.commentsEnabled() : true)
                .build();
        
        Chat chat = createChatUseCase.createChat(command);

        // Notify initial members
        if (request.initialMemberIds() != null) {
            for (UUID memberId : request.initialMemberIds()) {
                messagingTemplate.convertAndSend("/topic/chat/" + chat.getId().value() + "/members",
                        new ChatMemberEvent(chatEnrichmentService.enrichChat(chat.getId().value(), userId), userEnrichmentService.enrichUser(memberId), "JOIN"));
                
                // Notify user personally that they are now in a new chat
                realtimeNotificationPort.notifyChatEvent(
                        new ru.kubsu.borshchevyk.message.domain.model.value.UserId(memberId), 
                        chat.getId(), 
                        "JOINED"
                );
            }
        }

        return chatFacade.enrichChatResponse(chat, userId);
    }

    @Operation(summary = "Create a new private chat", description = "Creates or returns an existing private chat with the target user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Private chat created/returned successfully",
                    content = @Content(schema = @Schema(implementation = ChatResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request format",
                    content = @Content(schema = @Schema(implementation = MessageErrorResponse.class)))
    })
    @PostMapping("/private")
    public ChatResponse createPrivateChat(
            @RequestHeader("X-User-Id") @Parameter(description = "ID of the authenticated user") UUID userId,
            @RequestBody ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.request.CreatePrivateChatRequest request) {
        log.info("Request to create private chat from user {} to user {}", userId, request.targetUserId());
        
        Chat chat = ((ru.kubsu.borshchevyk.message.application.port.in.CreatePrivateChatUseCase) createChatUseCase).createPrivateChat(userId, request.targetUserId());
        
        // Notify target user about new private chat
        realtimeNotificationPort.notifyChatEvent(
                new ru.kubsu.borshchevyk.message.domain.model.value.UserId(request.targetUserId()), 
                chat.getId(), 
                "JOINED"
        );
        
        return chatFacade.enrichChatResponse(chat, userId);
    }

    @Operation(summary = "Get user chats", description = "Retrieves a list of chats for the authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of chats retrieved successfully")
    })
    @GetMapping
    public List<ChatResponse> getUserChats(
            @RequestHeader("X-User-Id") @Parameter(description = "ID of the authenticated user") UUID userId) {
        log.info("Request to get chats for user: {}", userId);
        List<Chat> chats = loadUserChatsUseCase.loadUserChats(new ru.kubsu.borshchevyk.message.domain.model.value.UserId(userId));
        return chatFacade.enrichChatResponses(chats, userId);
    }

    @Operation(summary = "Update member permissions", description = "Updates the permissions of a chat member.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Permissions updated successfully"),
            @ApiResponse(responseCode = "403", description = "User is not allowed to update permissions",
                    content = @Content(schema = @Schema(implementation = MessageErrorResponse.class)))
    })
    @PatchMapping("/{chatId}/members/{targetUserId}/permissions")
    public void updatePermissions(
            @PathVariable UUID chatId,
            @PathVariable UUID targetUserId,
            @RequestHeader("X-User-Id") @Parameter(description = "ID of the authenticated user") UUID requesterId,
            @RequestBody ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.request.UpdatePermissionsRequest request) {
        log.info("Request to update permissions for user {} in chat {} from user {}", targetUserId, chatId, requesterId);
        ru.kubsu.borshchevyk.message.application.dto.command.UpdatePermissionsCommand command = ru.kubsu.borshchevyk.message.application.dto.command.UpdatePermissionsCommand.builder()
                .chatId(chatId)
                .targetUserId(targetUserId)
                .requesterId(requesterId)
                .canSendMessages(request.canSendMessages())
                .canDeleteMessages(request.canDeleteMessages())
                .canInviteUsers(request.canInviteUsers())
                .canChangeInfo(request.canChangeInfo())
                .build();
        
        updateMemberPermissionsUseCase.updatePermissions(command);
        
        // Notify user about permissions update
        realtimeNotificationPort.notifyChatEvent(
                new ru.kubsu.borshchevyk.message.domain.model.value.UserId(targetUserId), 
                new ru.kubsu.borshchevyk.message.domain.model.value.ChatId(chatId), 
                "PERMISSIONS_UPDATED"
        );
    }

    @Operation(summary = "Clear chat history", description = "Clears history of the chat for the requester, or for all members if specified (private chats only).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Chat history cleared successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden to clear history for all in non-private chat",
                    content = @Content(schema = @Schema(implementation = MessageErrorResponse.class)))
    })
    @DeleteMapping("/{chatId}/history")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearChatHistory(
            @PathVariable UUID chatId,
            @RequestParam(defaultValue = "false") boolean forAll,
            @RequestHeader("X-User-Id") @Parameter(description = "ID of the authenticated user") UUID requesterId) {
        log.info("Request to clear history for chat {} from user {}, forAll: {}", chatId, requesterId, forAll);
        
        ru.kubsu.borshchevyk.message.application.dto.command.ClearChatHistoryCommand command = ru.kubsu.borshchevyk.message.application.dto.command.ClearChatHistoryCommand.builder()
                .chatId(chatId)
                .requesterId(requesterId)
                .forAll(forAll)
                .build();
                
        clearChatHistoryUseCase.clearChatHistory(command);
    }

    @Operation(summary = "Delete chat", description = "Deletes the chat (soft delete).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Chat deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden to delete chat",
                    content = @Content(schema = @Schema(implementation = MessageErrorResponse.class)))
    })
    @DeleteMapping("/{chatId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteChat(
            @PathVariable UUID chatId,
            @RequestHeader("X-User-Id") @Parameter(description = "ID of the authenticated user") UUID requesterId) {
        log.info("Request to delete chat {} from user {}", chatId, requesterId);
        
        ru.kubsu.borshchevyk.message.application.dto.command.DeleteChatCommand command = ru.kubsu.borshchevyk.message.application.dto.command.DeleteChatCommand.builder()
                .chatId(chatId)
                .requesterId(requesterId)
                .build();
                
        deleteChatUseCase.deleteChat(command);
    }

    @Operation(summary = "Invite user to chat", description = "Invites a user to the specified chat.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User invited successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden to invite",
                    content = @Content(schema = @Schema(implementation = MessageErrorResponse.class)))
    })
    @PostMapping("/{chatId}/members")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void inviteUser(
            @PathVariable UUID chatId,
            @RequestHeader("X-User-Id") UUID requesterId,
            @RequestBody ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.request.CreatePrivateChatRequest request) {
        log.info("Request to invite user {} to chat {} from user {}", request.targetUserId(), chatId, requesterId);
        InviteUserCommand command = InviteUserCommand.builder()
                .chatId(chatId)
                .requesterId(requesterId)
                .targetUserId(request.targetUserId())
                .build();
        inviteUserUseCase.inviteUser(command);
        
        // Notify existing members
        messagingTemplate.convertAndSend("/topic/chat/" + chatId + "/members",
                new ChatMemberEvent(chatEnrichmentService.enrichChat(chatId, requesterId), userEnrichmentService.enrichUser(request.targetUserId()), "JOIN"));
        
        // Notify the invited user personally
        realtimeNotificationPort.notifyChatEvent(
                new ru.kubsu.borshchevyk.message.domain.model.value.UserId(request.targetUserId()), 
                new ru.kubsu.borshchevyk.message.domain.model.value.ChatId(chatId), 
                "JOINED"
        );
    }

    @Operation(summary = "Kick user from chat", description = "Removes a user from the specified chat.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User removed successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden to remove",
                    content = @Content(schema = @Schema(implementation = MessageErrorResponse.class)))
    })
    @DeleteMapping("/{chatId}/members/{targetUserId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void kickUser(
            @PathVariable UUID chatId,
            @PathVariable UUID targetUserId,
            @RequestHeader("X-User-Id") UUID requesterId) {
        log.info("Request to kick user {} from chat {} from user {}", targetUserId, chatId, requesterId);
        KickUserCommand command = KickUserCommand.builder()
                .chatId(chatId)
                .requesterId(requesterId)
                .targetUserId(targetUserId)
                .build();
        kickUserUseCase.kickUser(command);
        
        // Notify remaining members
        messagingTemplate.convertAndSend("/topic/chat/" + chatId + "/members",
                new ChatMemberEvent(chatEnrichmentService.enrichChat(chatId, requesterId), userEnrichmentService.enrichUser(targetUserId), "LEAVE"));
        
        // Notify the kicked user personally
        realtimeNotificationPort.notifyChatEvent(
                new ru.kubsu.borshchevyk.message.domain.model.value.UserId(targetUserId), 
                new ru.kubsu.borshchevyk.message.domain.model.value.ChatId(chatId), 
                "KICKED"
        );
    }

    @Operation(summary = "Leave chat", description = "Leaves the specified chat.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Left chat successfully")
    })
    @DeleteMapping("/{chatId}/members/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void leaveChat(
            @PathVariable UUID chatId,
            @RequestHeader("X-User-Id") UUID requesterId) {
        log.info("Request to leave chat {} from user {}", chatId, requesterId);
        LeaveChatCommand command = LeaveChatCommand.builder()
                .chatId(chatId)
                .requesterId(requesterId)
                .build();
        leaveChatUseCase.leaveChat(command);
        
        // Notify remaining members
        messagingTemplate.convertAndSend("/topic/chat/" + chatId + "/members",
                new ChatMemberEvent(chatEnrichmentService.enrichChat(chatId, requesterId), userEnrichmentService.enrichUser(requesterId), "LEAVE"));
        
        // Notify the user personally (to sync other devices)
        realtimeNotificationPort.notifyChatEvent(
                new ru.kubsu.borshchevyk.message.domain.model.value.UserId(requesterId), 
                new ru.kubsu.borshchevyk.message.domain.model.value.ChatId(chatId), 
                "LEFT"
        );
    }

    @Operation(summary = "Generate invite link", description = "Generates a new invite link for the chat.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Link generated successfully")
    })
    @PostMapping("/{chatId}/invite-link")
    public String generateInviteLink(
            @PathVariable UUID chatId,
            @RequestHeader("X-User-Id") UUID requesterId) {
        log.info("Request to generate invite link for chat {} from user {}", chatId, requesterId);
        return generateInviteLinkUseCase.generateInviteLink(chatId, requesterId);
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
        Chat chat = joinChatByLinkUseCase.joinChatByLink(inviteCode, userId);
        messagingTemplate.convertAndSend("/topic/chat/" + chat.getId().value() + "/members",
                new ChatMemberEvent(chatEnrichmentService.enrichChat(chat.getId().value(), userId), userEnrichmentService.enrichUser(userId), "JOIN"));
        
        // Notify user personally
        realtimeNotificationPort.notifyChatEvent(
                new ru.kubsu.borshchevyk.message.domain.model.value.UserId(userId), 
                chat.getId(), 
                "JOINED"
        );
        return chatFacade.enrichChatResponse(chat, userId);
    }

    @Operation(summary = "Update chat info", description = "Updates the title and/or description of a chat.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Chat info updated successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden to update chat info",
                    content = @Content(schema = @Schema(implementation = MessageErrorResponse.class)))
    })
    @PatchMapping("/{chatId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateChatInfo(
            @PathVariable UUID chatId,
            @RequestHeader("X-User-Id") UUID requesterId,
            @RequestBody UpdateChatInfoRequest request) {
        log.info("Request to update chat info {} from user {}", chatId, requesterId);
        UpdateChatInfoCommand command = UpdateChatInfoCommand.builder()
                .chatId(chatId)
                .requesterId(requesterId)
                .title(request.title())
                .description(request.description())
                .commentsEnabled(request.commentsEnabled())
                .build();
        updateChatInfoUseCase.updateChatInfo(command);

        // Notify active viewers
        messagingTemplate.convertAndSend("/topic/chat/" + chatId + "/info",
                new ChatInfoEvent(chatEnrichmentService.enrichChat(chatId, requesterId), request.description(), request.commentsEnabled()));

        // Signal a refresh to all members in their personal queues
        Page<ChatMember> membersPage = loadChatMembersUseCase.loadChatMembers(chatId, requesterId, Pageable.unpaged());
        for (ChatMember member : membersPage.getContent()) {
            realtimeNotificationPort.notifyChatEvent(member.getUserId(), new ru.kubsu.borshchevyk.message.domain.model.value.ChatId(chatId), "INFO_UPDATED");
        }
    }

    @Operation(summary = "Update allowed reactions", description = "Updates the list of allowed reactions for a chat.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Reactions updated successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden to update reactions",
                    content = @Content(schema = @Schema(implementation = MessageErrorResponse.class)))
    })
    @PutMapping("/{chatId}/reactions")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateChatReactions(
            @PathVariable UUID chatId,
            @RequestHeader("X-User-Id") UUID requesterId,
            @RequestBody ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.request.UpdateChatReactionsRequest request) {
        log.info("Request to update chat reactions {} from user {}", chatId, requesterId);
        ru.kubsu.borshchevyk.message.application.dto.command.UpdateChatReactionsCommand command = ru.kubsu.borshchevyk.message.application.dto.command.UpdateChatReactionsCommand.builder()
                .chatId(chatId)
                .requesterId(requesterId)
                .allowedReactions(request.allowedReactions())
                .build();
        updateChatReactionsUseCase.updateChatReactions(command);

        // Notify active viewers
        messagingTemplate.convertAndSend("/topic/chat/" + chatId + "/settings",
                new ChatSettingsEvent(chatEnrichmentService.enrichChat(chatId, requesterId), request.allowedReactions()));
    }

    private final ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.facade.UserEnrichmentService userEnrichmentService;
    private final ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.facade.ChatEnrichmentService chatEnrichmentService;

    @Operation(summary = "Get chat members", description = "Retrieves a paginated list of members for a given chat.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of members retrieved successfully")
    })
    @GetMapping("/{chatId}/members")
    public Page<ChatMemberResponse> getChatMembers(
            @PathVariable UUID chatId,
            @RequestHeader("X-User-Id") UUID requesterId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        log.info("Request to get members for chat {} from user {} page {} size {}", chatId, requesterId, page, size);
        Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        Page<ChatMember> membersPage = loadChatMembersUseCase.loadChatMembers(chatId, requesterId, pageable);
        
        List<UUID> userIds = membersPage.getContent().stream()
                .map(m -> m.getUserId().value())
                .toList();
        
        java.util.Map<UUID, ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response.ShortUserDto> userMap = userEnrichmentService.enrichUsersToMap(userIds);

        return membersPage.map(member -> {
            ChatMemberResponse basic = presentationChatMapper.toMemberResponse(member);
            return new ChatMemberResponse(
                    basic.chatId(),
                    basic.userId(),
                    userMap.get(member.getUserId().value()),
                    basic.role(),
                    basic.joinedAt(),
                    basic.canSendMessages(),
                    basic.canDeleteMessages(),
                    basic.canInviteUsers(),
                    basic.canChangeInfo(),
                    basic.lastReadMessageId()
            );
        });
    }
}
