package ru.kubsu.borshchevyk.user.application.dto.command;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record GetUsersBatchCommand(
    List<UUID> userIds,
    String requesterId
) {}
