package ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import ru.kubsu.borshchevyk.message.application.dto.command.AddReactionCommand;
import ru.kubsu.borshchevyk.message.application.dto.command.RemoveReactionCommand;
import ru.kubsu.borshchevyk.message.application.port.in.AddReactionUseCase;
import ru.kubsu.borshchevyk.message.application.port.in.RemoveReactionUseCase;
import ru.kubsu.borshchevyk.message.domain.event.reaction.ReactionEvent;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response.ShortUserDto;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.facade.UserEnrichmentService;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/chats/{chatId}/messages")
@RequiredArgsConstructor
@Tag(name = "Message Reactions", description = "Endpoints for managing message reactions")
public class MessageReactionController {

    private final AddReactionUseCase addReactionUseCase;
    private final RemoveReactionUseCase removeReactionUseCase;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserEnrichmentService userEnrichmentService;

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
        messagingTemplate.convertAndSend("/topic/chat/" + chatId + "/reactions", new ReactionEvent(messageId, userEnrichmentService.enrichUser(userId), reaction, true));
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
        ShortUserDto user = userEnrichmentService.enrichUser(userId);
        messagingTemplate.convertAndSend("/topic/chat/" + chatId + "/reactions", new ReactionEvent(messageId, user, reaction, false));
    }
}
