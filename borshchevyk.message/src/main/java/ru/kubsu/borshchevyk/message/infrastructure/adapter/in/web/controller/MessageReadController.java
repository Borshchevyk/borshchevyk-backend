package ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.kubsu.borshchevyk.message.infrastructure.websocket.WebSocketEventBroadcaster;
import org.springframework.web.bind.annotation.*;
import ru.kubsu.borshchevyk.message.application.dto.command.ReadMessageCommand;
import ru.kubsu.borshchevyk.message.application.dto.query.LoadMessageCommentsQuery;
import ru.kubsu.borshchevyk.message.application.dto.query.LoadMessageReadersQuery;
import ru.kubsu.borshchevyk.message.application.port.in.LoadMessageCommentsUseCase;
import ru.kubsu.borshchevyk.message.application.port.in.LoadMessageReadersUseCase;
import ru.kubsu.borshchevyk.message.application.port.in.ReadMessageUseCase;
import ru.kubsu.borshchevyk.message.application.port.out.PublishChatEventPort;
import ru.kubsu.borshchevyk.message.domain.event.message.ReadMessageEvent;
import ru.kubsu.borshchevyk.message.domain.model.message.Message;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response.MessageResponse;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response.ShortUserDto;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.facade.UserEnrichmentService;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.mapper.PresentationMessageMapper;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/chats/{chatId}/messages")
@RequiredArgsConstructor
@Tag(name = "Message Read & Comments", description = "Endpoints for managing message reading and comments")
public class MessageReadController {

    private final ReadMessageUseCase readMessageUseCase;
    private final LoadMessageReadersUseCase loadMessageReadersUseCase;
    private final LoadMessageCommentsUseCase loadMessageCommentsUseCase;
    private final PresentationMessageMapper presentationMessageMapper;
    private final UserEnrichmentService userEnrichmentService;
    private final PublishChatEventPort PublishChatEventPort;
    private final WebSocketEventBroadcaster eventBroadcaster;

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
        ReadMessageCommand command = ReadMessageCommand.builder()
                .chatId(chatId)
                .messageId(messageId)
                .requesterId(userId)
                .build();
                
        readMessageUseCase.readMessage(command);

        ShortUserDto user = userEnrichmentService.enrichUser(userId);
        ReadMessageEvent event = new ReadMessageEvent(chatId, user, messageId);
        eventBroadcaster.broadcastToChatMembers(chatId, "MESSAGE_READ", event);
        
        PublishChatEventPort.publishChatEvent(
                new UserId(userId),
                new ChatId(chatId),
                "READ"
        );
    }

    @Operation(summary = "Get message readers", description = "Retrieves a list of users who have read the message.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of readers retrieved successfully")
    })
    @GetMapping("/{messageId}/readers")
    public List<ShortUserDto> getMessageReaders(
            @PathVariable UUID chatId,
            @PathVariable UUID messageId,
            @RequestHeader("X-User-Id") UUID userId) {
        log.info("Request to get readers of message {} in chat {} by user {}", messageId, chatId, userId);
        LoadMessageReadersQuery query = new LoadMessageReadersQuery(chatId, messageId, userId);
        List<UUID> readerIds = loadMessageReadersUseCase.loadMessageReaders(query);
        return userEnrichmentService.enrichUsers(readerIds);
    }

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
        LoadMessageCommentsQuery query = new LoadMessageCommentsQuery(chatId, messageId, userId, page, size);
        List<Message> comments = loadMessageCommentsUseCase.loadMessageComments(query);
        return presentationMessageMapper.toResponseList(comments, userId);
    }
}