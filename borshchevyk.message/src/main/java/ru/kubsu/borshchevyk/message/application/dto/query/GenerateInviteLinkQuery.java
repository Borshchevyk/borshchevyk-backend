package ru.kubsu.borshchevyk.message.application.dto.query;

import java.util.UUID;

public record GenerateInviteLinkQuery(
        UUID chatId,
        UUID requesterId
) { }