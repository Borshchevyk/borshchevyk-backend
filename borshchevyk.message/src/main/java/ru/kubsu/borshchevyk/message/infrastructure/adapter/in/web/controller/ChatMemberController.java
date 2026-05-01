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
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import ru.kubsu.borshchevyk.message.application.dto.command.InviteUserCommand;
import ru.kubsu.borshchevyk.message.application.dto.command.KickUserCommand;
import ru.kubsu.borshchevyk.message.application.dto.command.LeaveChatCommand;
import ru.kubsu.borshchevyk.message.application.dto.command.UpdatePermissionsCommand;
import ru.kubsu.borshchevyk.message.application.port.in.InviteUserUseCase;
import ru.kubsu.borshchevyk.message.application.port.in.KickUserUseCase;
import ru.kubsu.borshchevyk.message.application.port.in.LeaveChatUseCase;
import ru.kubsu.borshchevyk.message.application.port.in.LoadChatMembersUseCase;
import ru.kubsu.borshchevyk.message.application.port.in.UpdateMemberPermissionsUseCase;
import ru.kubsu.borshchevyk.message.application.port.out.ChatEventPublisherPort;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatMember;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.request.CreatePrivateChatRequest;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.request.UpdatePermissionsRequest;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response.ChatMemberResponse;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response.ShortUserDto;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.facade.ChatEnrichmentService;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.facade.UserEnrichmentService;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.mapper.PresentationChatMapper;
import ru.kubsu.borshchevyk.message.infrastructure.exception.MessageErrorResponse;
import ru.kubsu.borshchevyk.message.infrastructure.websocket.dto.ChatMemberEvent;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Controller for managing chat members and permissions.
 *
 * @author Aleksey Timko
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/chats")
@RequiredArgsConstructor
@Tag(name = "Chat Members", description = "Endpoints for managing chat members")
public class ChatMemberController {

    private final InviteUserUseCase inviteUserUseCase;
    private final KickUserUseCase kickUserUseCase;
    private final LeaveChatUseCase leaveChatUseCase;
    private final UpdateMemberPermissionsUseCase updateMemberPermissionsUseCase;
    private final LoadChatMembersUseCase loadChatMembersUseCase;
    private final PresentationChatMapper presentationChatMapper;
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatEventPublisherPort chatEventPublisherPort;
    private final ChatEnrichmentService chatEnrichmentService;
    private final UserEnrichmentService userEnrichmentService;

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
        
        Map<UUID, ShortUserDto> userMap = userEnrichmentService.enrichUsersToMap(userIds);

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
            @RequestBody CreatePrivateChatRequest request) {
        log.info("Request to invite user {} to chat {} from user {}", request.targetUserId(), chatId, requesterId);
        InviteUserCommand command = InviteUserCommand.builder()
                .chatId(chatId)
                .requesterId(requesterId)
                .targetUserId(request.targetUserId())
                .build();
        inviteUserUseCase.inviteUser(command);
        
        messagingTemplate.convertAndSend("/topic/chat/" + chatId + "/members",
                new ChatMemberEvent(chatEnrichmentService.enrichChat(chatId, requesterId), userEnrichmentService.enrichUser(request.targetUserId()), "JOIN"));
        
        chatEventPublisherPort.publishChatEvent(
                new UserId(request.targetUserId()), 
                new ChatId(chatId), 
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
        
        messagingTemplate.convertAndSend("/topic/chat/" + chatId + "/members",
                new ChatMemberEvent(chatEnrichmentService.enrichChat(chatId, requesterId), userEnrichmentService.enrichUser(targetUserId), "LEAVE"));
        
        chatEventPublisherPort.publishChatEvent(
                new UserId(targetUserId), 
                new ChatId(chatId), 
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
        
        messagingTemplate.convertAndSend("/topic/chat/" + chatId + "/members",
                new ChatMemberEvent(chatEnrichmentService.enrichChat(chatId, requesterId), userEnrichmentService.enrichUser(requesterId), "LEAVE"));
        
        chatEventPublisherPort.publishChatEvent(
                new UserId(requesterId), 
                new ChatId(chatId), 
                "LEFT"
        );
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
            @RequestBody UpdatePermissionsRequest request) {
        log.info("Request to update permissions for user {} in chat {} from user {}", targetUserId, chatId, requesterId);
        UpdatePermissionsCommand command = UpdatePermissionsCommand.builder()
                .chatId(chatId)
                .targetUserId(targetUserId)
                .requesterId(requesterId)
                .canSendMessages(request.canSendMessages())
                .canDeleteMessages(request.canDeleteMessages())
                .canInviteUsers(request.canChangeInfo())
                .canChangeInfo(request.canChangeInfo())
                .build();
        
        updateMemberPermissionsUseCase.updatePermissions(command);
        
        chatEventPublisherPort.publishChatEvent(
                new UserId(targetUserId), 
                new ChatId(chatId), 
                "PERMISSIONS_UPDATED"
        );
    }
}
