package ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.kubsu.borshchevyk.message.application.port.in.SearchChatsUseCase;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response.ChatResponse;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response.GlobalSearchResponse;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response.ShortUserDto;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.facade.ChatFacade;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.out.grpc.UserGrpcClient;
import ru.kubsu.borshchevyk.grpc.UserResponse;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author Aleksey Timko
 * @since 2026-05-01
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
@Tag(name = "Global Search", description = "Unified search for users and public chats")
public class GlobalSearchController {

    private final SearchChatsUseCase searchChatsUseCase;
    private final ChatFacade chatFacade;
    private final UserGrpcClient userGrpcClient;

    @Operation(summary = "Search users and public chats globally", description = "Searches users by tag/name and public groups/channels by name")
    @ApiResponse(responseCode = "200", description = "Global search results")
    @GetMapping
    public GlobalSearchResponse search(
            @RequestParam String query,
            @RequestHeader(value = "X-User-Id", required = false) UUID requesterId) {
        log.info("Request to search globally with query: {}", query);
        
        List<ru.kubsu.borshchevyk.message.domain.model.chat.Chat> foundChats = searchChatsUseCase.searchPublicChats(query);
        List<ChatResponse> chatResponses = chatFacade.enrichChatResponses(foundChats, requesterId);

        List<UserResponse> userResponses = userGrpcClient.searchUsers(query, requesterId);
        List<ShortUserDto> userDtos = userResponses.stream()
                .map(u -> new ShortUserDto(
                        UUID.fromString(u.getUserId()),
                        u.getFirstName(),
                        u.getLastName(),
                        u.getTag(),
                        u.getAvatarUrl().isEmpty() ? null : u.getAvatarUrl()
                ))
                .collect(Collectors.toList());

        return new GlobalSearchResponse(chatResponses, userDtos);
    }
}
