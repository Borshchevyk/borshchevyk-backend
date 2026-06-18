package ru.kubsu.borshchevyk.message.application.dto.command;

import java.util.UUID;

public record JoinChatByLinkCommand(
        String inviteCode,
        UUID userId)
{ }