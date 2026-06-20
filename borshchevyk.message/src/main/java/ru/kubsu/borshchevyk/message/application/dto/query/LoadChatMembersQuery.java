package ru.kubsu.borshchevyk.message.application.dto.query;

import org.springframework.data.domain.Pageable;

import java.util.UUID;

public record LoadChatMembersQuery(
        UUID chatId,
        UUID requesterId,
        Pageable pageable
) { }
