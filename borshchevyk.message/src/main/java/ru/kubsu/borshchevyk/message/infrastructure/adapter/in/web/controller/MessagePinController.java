package ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import ru.kubsu.borshchevyk.message.application.dto.command.PinMessageCommand;
import ru.kubsu.borshchevyk.message.application.dto.command.UnpinMessageCommand;
import ru.kubsu.borshchevyk.message.application.dto.query.LoadPinnedMessagesQuery;
import ru.kubsu.borshchevyk.message.application.port.in.LoadPinnedMessagesUseCase;
import ru.kubsu.borshchevyk.message.application.port.in.PinMessageUseCase;
import ru.kubsu.borshchevyk.message.application.port.in.UnpinMessageUseCase;
import ru.kubsu.borshchevyk.message.domain.model.message.Message;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response.MessageResponse;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.mapper.PresentationMessageMapper;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/chats/{chatId}/messages")
@RequiredArgsConstructor
@Tag(name = "Message Pins", description = "Endpoints for managing message pins")
public class MessagePinController {

    private final PinMessageUseCase pinMessageUseCase;
    private final UnpinMessageUseCase unpinMessageUseCase;
    private final LoadPinnedMessagesUseCase loadPinnedMessagesUseCase;
    private final PresentationMessageMapper presentationMessageMapper;
    private final SimpMessagingTemplate messagingTemplate;

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
        LoadPinnedMessagesQuery query = new LoadPinnedMessagesQuery(chatId, userId);
        List<Message> messages = loadPinnedMessagesUseCase.loadPinnedMessages(query);
        return presentationMessageMapper.toResponseList(messages, userId);
    }
}
