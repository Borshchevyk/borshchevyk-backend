package ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import ru.kubsu.borshchevyk.message.application.dto.command.ClearChatHistoryCommand;
import ru.kubsu.borshchevyk.message.application.dto.command.CreateChatCommand;
import ru.kubsu.borshchevyk.message.application.dto.command.DeleteChatCommand;
import ru.kubsu.borshchevyk.message.application.port.in.ClearChatHistoryUseCase;
import ru.kubsu.borshchevyk.message.application.port.in.CreateChatUseCase;
import ru.kubsu.borshchevyk.message.application.port.in.CreatePrivateChatUseCase;
import ru.kubsu.borshchevyk.message.application.port.in.DeleteChatUseCase;
import ru.kubsu.borshchevyk.message.application.port.in.LoadUserChatsUseCase;
import ru.kubsu.borshchevyk.message.application.port.out.RealtimeNotificationPort;
import ru.kubsu.borshchevyk.message.domain.model.chat.Chat;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.request.CreateChatRequest;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.request.CreatePrivateChatRequest;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response.ChatResponse;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.facade.ChatEnrichmentService;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.facade.ChatFacade;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.facade.UserEnrichmentService;
import ru.kubsu.borshchevyk.message.infrastructure.exception.MessageErrorResponse;
import ru.kubsu.borshchevyk.message.infrastructure.websocket.dto.ChatMemberEvent;

import java.util.List;
import java.util.UUID;

/**
 * Controller for managing chats.
 * Handles HTTP requests for creation, deletion, and history clearance.
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
    private final ClearChatHistoryUseCase clearChatHistoryUseCase;
    private final DeleteChatUseCase deleteChatUseCase;
    private final LoadUserChatsUseCase loadUserChatsUseCase;
    private final ChatFacade chatFacade;
    private final SimpMessagingTemplate messagingTemplate;
    private final RealtimeNotificationPort realtimeNotificationPort;
    private final ChatEnrichmentService chatEnrichmentService;
    private final UserEnrichmentService userEnrichmentService;

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
            @Valid @RequestBody CreateChatRequest request) {
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

        if (request.initialMemberIds() != null) {
            for (UUID memberId : request.initialMemberIds()) {
                messagingTemplate.convertAndSend("/topic/chat/" + chat.getId().value() + "/members",
                        new ChatMemberEvent(chatEnrichmentService.enrichChat(chat.getId().value(), userId), userEnrichmentService.enrichUser(memberId), "JOIN"));
                
                realtimeNotificationPort.notifyChatEvent(
                        new UserId(memberId), 
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
            @RequestBody CreatePrivateChatRequest request) {
        log.info("Request to create private chat from user {} to user {}", userId, request.targetUserId());
        
        Chat chat = ((CreatePrivateChatUseCase) createChatUseCase).createPrivateChat(userId, request.targetUserId());
        
        realtimeNotificationPort.notifyChatEvent(
                new UserId(request.targetUserId()), 
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
        List<Chat> chats = loadUserChatsUseCase.loadUserChats(new UserId(userId));
        return chatFacade.enrichChatResponses(chats, userId);
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
        
        ClearChatHistoryCommand command = ClearChatHistoryCommand.builder()
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
        
        DeleteChatCommand command = DeleteChatCommand.builder()
                .chatId(chatId)
                .requesterId(requesterId)
                .build();
                
        deleteChatUseCase.deleteChat(command);
    }
}
