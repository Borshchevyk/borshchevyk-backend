package ru.kubsu.borshchevyk.auth.application.dto.command;

import lombok.Builder;

import java.util.UUID;

@Builder
public record VerifyCommand(
        UUID userId,
        String signature
) { }
