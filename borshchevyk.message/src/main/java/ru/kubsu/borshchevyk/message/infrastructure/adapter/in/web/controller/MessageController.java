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
import org.springframework.web.bind.annotation.*;
import ru.kubsu.borshchevyk.message.application.dto.command.SendMessageCommand;
import ru.kubsu.borshchevyk.message.application.port.in.LoadChatHistoryUseCase;
import ru.kubsu.borshchevyk.message.application.port.in.SendMessageUseCase;
import ru.kubsu.borshchevyk.message.domain.model.message.Message;
import ru.kubsu.borshchevyk.message.domain.model.message.MessageSource;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.request.SendMessageRequest;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response.MessageResponse;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.mapper.PresentationMessageMapper;
import ru.kubsu.borshchevyk.message.infrastructure.exception.MessageErrorResponse;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/chats/{chatId}/messages")
@RequiredArgsConstructor
@Tag(name = "Message", description = "Endpoints for managing messages in chats")
public class MessageController {

    private final SendMessageUseCase sendMessageUseCase;
    private final LoadChatHistoryUseCase loadChatHistoryUseCase;
    private final ru.kubsu.borshchevyk.message.application.port.in.DeleteMessageUseCase deleteMessageUseCase;
    private final PresentationMessageMapper presentationMessageMapper;

    @Operation(summary = "Send a message", description = "Sends a new message to a specific chat.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Message sent successfully",
                    content = @Content(schema = @Schema(implementation = MessageResponse.class))),
            @ApiResponse(responseCode = "403", description = "User is not a member of the chat",
                    content = @Content(schema = @Schema(implementation = MessageErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Chat not found",
                    content = @Content(schema = @Schema(implementation = MessageErrorResponse.class)))
    })
    @PostMapping
    public MessageResponse sendMessage(
            @PathVariable UUID chatId,
            @RequestHeader("X-User-Id") @Parameter(description = "ID of the authenticated user") UUID userId,
            @RequestBody SendMessageRequest request) {
        log.info("Request to send message to chat {} from user {}", chatId, userId);
        SendMessageCommand command = SendMessageCommand.builder()
                .chatId(chatId)
                .authorId(userId)
                .text(request.text())
                .source(request.source() != null ? request.source() : MessageSource.ONLINE)
                .build();
        
        Message message = sendMessageUseCase.sendMessage(command);
        return presentationMessageMapper.toResponse(message);
    }

    @Operation(summary = "Load chat history", description = "Loads paginated chat history for a specific chat.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chat history loaded successfully")
    })
    @GetMapping
    public List<MessageResponse> loadChatHistory(
            @PathVariable UUID chatId,
            @RequestHeader("X-User-Id") @Parameter(description = "ID of the authenticated user") UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        log.info("Request to load chat history for chat {} from user {} (page: {}, size: {})", chatId, userId, page, size);
        List<Message> messages = loadChatHistoryUseCase.loadChatHistory(chatId, userId, page, size);
        return presentationMessageMapper.toResponseList(messages);
    }

    @Operation(summary = "Delete a message", description = "Deletes a specific message.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Message deleted successfully"),
            @ApiResponse(responseCode = "403", description = "User is not allowed to delete this message",
                    content = @Content(schema = @Schema(implementation = MessageErrorResponse.class)))
    })
    @DeleteMapping("/{messageId}")
    public void deleteMessage(
            @PathVariable UUID chatId,
            @PathVariable UUID messageId,
            @RequestHeader("X-User-Id") @Parameter(description = "ID of the authenticated user") UUID userId,
            @RequestParam(defaultValue = "false") boolean forAll) {
        log.info("Request to delete message {} in chat {} from user {} (forAll: {})", messageId, chatId, userId, forAll);
        ru.kubsu.borshchevyk.message.application.dto.command.DeleteMessageCommand command = ru.kubsu.borshchevyk.message.application.dto.command.DeleteMessageCommand.builder()
                .messageId(messageId)
                .requesterId(userId)
                .forAll(forAll)
                .build();
        
        deleteMessageUseCase.deleteMessage(command);
    }
}
