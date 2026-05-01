package ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.controller;

import io.swagger.v3.oas.annotations.Operation;
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
import ru.kubsu.borshchevyk.message.application.dto.command.UpdateChatInfoCommand;
import ru.kubsu.borshchevyk.message.application.dto.command.UpdateChatReactionsCommand;
import ru.kubsu.borshchevyk.message.application.port.in.LoadChatMembersUseCase;
import ru.kubsu.borshchevyk.message.application.port.in.UpdateChatInfoUseCase;
import ru.kubsu.borshchevyk.message.application.port.in.UpdateChatReactionsUseCase;
import ru.kubsu.borshchevyk.message.application.port.out.ChatEventPublisherPort;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatMember;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.request.UpdateChatInfoRequest;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.request.UpdateChatReactionsRequest;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.facade.ChatEnrichmentService;
import ru.kubsu.borshchevyk.message.infrastructure.exception.MessageErrorResponse;
import ru.kubsu.borshchevyk.message.infrastructure.websocket.dto.ChatInfoEvent;
import ru.kubsu.borshchevyk.message.infrastructure.websocket.dto.ChatSettingsEvent;

import java.util.UUID;

/**
 * Controller for managing chat settings.
 *
 * @author Aleksey Timko
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/chats")
@RequiredArgsConstructor
@Tag(name = "Chat Settings", description = "Endpoints for managing chat settings")
public class ChatSettingsController {

    private final UpdateChatInfoUseCase updateChatInfoUseCase;
    private final UpdateChatReactionsUseCase updateChatReactionsUseCase;
    private final LoadChatMembersUseCase loadChatMembersUseCase;
    private final ChatEnrichmentService chatEnrichmentService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatEventPublisherPort chatEventPublisherPort;

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

        messagingTemplate.convertAndSend("/topic/chat/" + chatId + "/info",
                new ChatInfoEvent(chatEnrichmentService.enrichChat(chatId, requesterId), request.description(), request.commentsEnabled()));

        Page<ChatMember> membersPage = loadChatMembersUseCase.loadChatMembers(chatId, requesterId, Pageable.unpaged());
        for (ChatMember member : membersPage.getContent()) {
            chatEventPublisherPort.publishChatEvent(member.getUserId(), new ChatId(chatId), "INFO_UPDATED");
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
            @RequestBody UpdateChatReactionsRequest request) {
        log.info("Request to update chat reactions {} from user {}", chatId, requesterId);
        UpdateChatReactionsCommand command = UpdateChatReactionsCommand.builder()
                .chatId(chatId)
                .requesterId(requesterId)
                .allowedReactions(request.allowedReactions())
                .build();
        updateChatReactionsUseCase.updateChatReactions(command);

        messagingTemplate.convertAndSend("/topic/chat/" + chatId + "/settings",
                new ChatSettingsEvent(chatEnrichmentService.enrichChat(chatId, requesterId), request.allowedReactions()));
    }
}
