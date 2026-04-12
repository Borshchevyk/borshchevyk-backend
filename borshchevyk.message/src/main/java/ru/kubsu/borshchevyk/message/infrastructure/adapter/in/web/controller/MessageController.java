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

import org.springframework.messaging.simp.SimpMessagingTemplate;
import ru.kubsu.borshchevyk.message.application.port.in.ReadMessageUseCase;
import ru.kubsu.borshchevyk.message.infrastructure.websocket.dto.ReadReceiptEvent;

import ru.kubsu.borshchevyk.message.application.port.in.LoadPinnedMessagesUseCase;
import ru.kubsu.borshchevyk.message.application.port.in.PinMessageUseCase;
import ru.kubsu.borshchevyk.message.application.port.in.UnpinMessageUseCase;
import ru.kubsu.borshchevyk.message.application.dto.command.PinMessageCommand;
import ru.kubsu.borshchevyk.message.application.dto.command.UnpinMessageCommand;

import ru.kubsu.borshchevyk.message.application.port.in.LoadMessageReadersUseCase;
import ru.kubsu.borshchevyk.message.application.port.in.AddReactionUseCase;
import ru.kubsu.borshchevyk.message.application.port.in.RemoveReactionUseCase;
import ru.kubsu.borshchevyk.message.application.dto.command.AddReactionCommand;
import ru.kubsu.borshchevyk.message.application.dto.command.RemoveReactionCommand;
import ru.kubsu.borshchevyk.message.infrastructure.websocket.dto.ReactionEvent;

import ru.kubsu.borshchevyk.message.application.port.in.LoadMessageCommentsUseCase;

@Slf4j
@RestController
@RequestMapping("/api/v1/chats/{chatId}/messages")
@RequiredArgsConstructor
@Tag(name = "Message", description = "Endpoints for managing messages in chats")
public class MessageController {

    private final SendMessageUseCase sendMessageUseCase;
    private final LoadChatHistoryUseCase loadChatHistoryUseCase;
    private final ru.kubsu.borshchevyk.message.application.port.in.DeleteMessageUseCase deleteMessageUseCase;
    private final ReadMessageUseCase readMessageUseCase;
    private final PinMessageUseCase pinMessageUseCase;
    private final UnpinMessageUseCase unpinMessageUseCase;
    private final LoadPinnedMessagesUseCase loadPinnedMessagesUseCase;
    private final AddReactionUseCase addReactionUseCase;
    private final RemoveReactionUseCase removeReactionUseCase;
    private final LoadMessageReadersUseCase loadMessageReadersUseCase;
    private final LoadMessageCommentsUseCase loadMessageCommentsUseCase;
    private final PresentationMessageMapper presentationMessageMapper;
    private final SimpMessagingTemplate messagingTemplate;

    @Operation(summary = "Get message comments", description = "Retrieves paginated comments for a specific message.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comments retrieved successfully")
    })
    @GetMapping("/{messageId}/comments")
    public List<MessageResponse> getMessageComments(
            @PathVariable UUID chatId,
            @PathVariable UUID messageId,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        log.info("Request to get comments for message {} in chat {} by user {} (page: {}, size: {})", messageId, chatId, userId, page, size);
        List<Message> comments = loadMessageCommentsUseCase.loadMessageComments(chatId, messageId, userId, page, size);
        return presentationMessageMapper.toResponseList(comments);
    }

    @Operation(summary = "Get message readers", description = "Retrieves a list of user IDs who have read the message.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of readers retrieved successfully")
    })
    @GetMapping("/{messageId}/readers")
    public List<UUID> getMessageReaders(
            @PathVariable UUID chatId,
            @PathVariable UUID messageId,
            @RequestHeader("X-User-Id") UUID userId) {
        log.info("Request to get readers of message {} in chat {} by user {}", messageId, chatId, userId);
        return loadMessageReadersUseCase.loadMessageReaders(chatId, messageId, userId);
    }

    @Operation(summary = "Add reaction", description = "Adds a reaction to a message.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Reaction added successfully")
    })
    @PostMapping("/{messageId}/reactions/{reaction}")
    @ResponseStatus(org.springframework.http.HttpStatus.NO_CONTENT)
    public void addReaction(
            @PathVariable UUID chatId,
            @PathVariable UUID messageId,
            @PathVariable String reaction,
            @RequestHeader("X-User-Id") UUID userId) {
        log.info("Request to add reaction {} to message {} in chat {} by user {}", reaction, messageId, chatId, userId);
        AddReactionCommand command = AddReactionCommand.builder()
                .chatId(chatId)
                .messageId(messageId)
                .requesterId(userId)
                .reaction(reaction)
                .build();
        addReactionUseCase.addReaction(command);
        messagingTemplate.convertAndSend("/topic/chat/" + chatId + "/reactions", new ReactionEvent(messageId, userId, reaction, true));
    }

    @Operation(summary = "Remove reaction", description = "Removes a reaction from a message.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Reaction removed successfully")
    })
    @DeleteMapping("/{messageId}/reactions/{reaction}")
    @ResponseStatus(org.springframework.http.HttpStatus.NO_CONTENT)
    public void removeReaction(
            @PathVariable UUID chatId,
            @PathVariable UUID messageId,
            @PathVariable String reaction,
            @RequestHeader("X-User-Id") UUID userId) {
        log.info("Request to remove reaction {} from message {} in chat {} by user {}", reaction, messageId, chatId, userId);
        RemoveReactionCommand command = RemoveReactionCommand.builder()
                .chatId(chatId)
                .messageId(messageId)
                .requesterId(userId)
                .reaction(reaction)
                .build();
        removeReactionUseCase.removeReaction(command);
        messagingTemplate.convertAndSend("/topic/chat/" + chatId + "/reactions", new ReactionEvent(messageId, userId, reaction, false));
    }

    @Operation(summary = "Pin message", description = "Pins a message in the chat (max 5).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Message pinned successfully")
    })
    @PostMapping("/{messageId}/pin")
    @ResponseStatus(org.springframework.http.HttpStatus.NO_CONTENT)
    public void pinMessage(
            @PathVariable UUID chatId,
            @PathVariable UUID messageId,
            @RequestHeader("X-User-Id") UUID userId) {
        log.info("Request to pin message {} in chat {} by user {}", messageId, chatId, userId);
        PinMessageCommand command = PinMessageCommand.builder()
                .chatId(chatId)
                .messageId(messageId)
                .requesterId(userId)
                .build();
        pinMessageUseCase.pinMessage(command);
        messagingTemplate.convertAndSend("/topic/chat/" + chatId + "/pin", messageId);
    }

    @Operation(summary = "Unpin message", description = "Unpins a message in the chat.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Message unpinned successfully")
    })
    @PostMapping("/{messageId}/unpin")
    @ResponseStatus(org.springframework.http.HttpStatus.NO_CONTENT)
    public void unpinMessage(
            @PathVariable UUID chatId,
            @PathVariable UUID messageId,
            @RequestHeader("X-User-Id") UUID userId) {
        log.info("Request to unpin message {} in chat {} by user {}", messageId, chatId, userId);
        UnpinMessageCommand command = UnpinMessageCommand.builder()
                .chatId(chatId)
                .messageId(messageId)
                .requesterId(userId)
                .build();
        unpinMessageUseCase.unpinMessage(command);
        messagingTemplate.convertAndSend("/topic/chat/" + chatId + "/unpin", messageId);
    }

    @Operation(summary = "Get pinned messages", description = "Gets all pinned messages for a chat.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pinned messages retrieved successfully")
    })
    @GetMapping("/pinned")
    public List<MessageResponse> getPinnedMessages(
            @PathVariable UUID chatId,
            @RequestHeader("X-User-Id") UUID userId) {
        log.info("Request to get pinned messages in chat {} by user {}", chatId, userId);
        List<Message> messages = loadPinnedMessagesUseCase.loadPinnedMessages(chatId, userId);
        return presentationMessageMapper.toResponseList(messages);
    }

    @Operation(summary = "Mark message as read", description = "Marks a specific message as read by the user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Message marked as read successfully")
    })
    @PostMapping("/{messageId}/read")
    @ResponseStatus(org.springframework.http.HttpStatus.NO_CONTENT)
    public void readMessage(
            @PathVariable UUID chatId,
            @PathVariable UUID messageId,
            @RequestHeader("X-User-Id") UUID userId) {
        log.info("Request to mark message {} as read in chat {} by user {}", messageId, chatId, userId);
        ru.kubsu.borshchevyk.message.application.dto.command.ReadMessageCommand command = ru.kubsu.borshchevyk.message.application.dto.command.ReadMessageCommand.builder()
                .chatId(chatId)
                .messageId(messageId)
                .requesterId(userId)
                .build();
                
        readMessageUseCase.readMessage(command);

        // Broadcast to WS
        ReadReceiptEvent event = new ReadReceiptEvent(userId, messageId);
        messagingTemplate.convertAndSend("/topic/chat/" + chatId + "/read", event);
    }

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
                .forwardedFromChatId(request.forwardedFromChatId())
                .forwardedFromUserId(request.forwardedFromUserId())
                .parentMessageId(request.parentMessageId())
                .attachmentIds(request.attachmentIds())
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
