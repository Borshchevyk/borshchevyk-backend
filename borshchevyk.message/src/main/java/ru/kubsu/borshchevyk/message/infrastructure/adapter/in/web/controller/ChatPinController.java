package ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.kubsu.borshchevyk.message.application.dto.command.PinChatCommand;
import ru.kubsu.borshchevyk.message.application.dto.command.UnpinChatCommand;
import ru.kubsu.borshchevyk.message.application.port.in.PinChatUseCase;
import ru.kubsu.borshchevyk.message.application.port.in.UnpinChatUseCase;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/chats")
@RequiredArgsConstructor
@Tag(name = "Chat Pins", description = "Endpoints for managing chat pins")
public class ChatPinController {

    private final PinChatUseCase pinChatUseCase;
    private final UnpinChatUseCase unpinChatUseCase;

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
        PinChatCommand command = PinChatCommand.builder()
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
        UnpinChatCommand command = UnpinChatCommand.builder()
                .chatId(chatId)
                .requesterId(userId)
                .build();
        unpinChatUseCase.unpinChat(command);
    }
}