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
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.kubsu.borshchevyk.message.application.dto.command.CreateChatCommand;
import ru.kubsu.borshchevyk.message.application.port.in.ClearChatHistoryUseCase;
import ru.kubsu.borshchevyk.message.application.port.in.CreateChatUseCase;
import ru.kubsu.borshchevyk.message.application.port.in.DeleteChatUseCase;
import ru.kubsu.borshchevyk.message.domain.model.chat.Chat;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.request.CreateChatRequest;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response.ChatResponse;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.mapper.PresentationChatMapper;

import java.util.UUID;

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
    private final PresentationChatMapper presentationChatMapper;

    @Operation(summary = "Create a new chat", description = "Creates a new chat with the given type, title, description, and initial members.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chat created successfully",
                    content = @Content(schema = @Schema(implementation = ChatResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request format")
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
                .build();
        
        Chat chat = createChatUseCase.createChat(command);
        return presentationChatMapper.toResponse(chat);
    }

    @Operation(summary = "Update member permissions", description = "Updates the permissions of a chat member.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Permissions updated successfully"),
            @ApiResponse(responseCode = "403", description = "User is not allowed to update permissions")
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
    }

    @Operation(summary = "Clear chat history", description = "Clears history of the chat for the requester, or for all members if specified (private chats only).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Chat history cleared successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden to clear history for all in non-private chat")
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
            @ApiResponse(responseCode = "403", description = "Forbidden to delete chat")
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
}
