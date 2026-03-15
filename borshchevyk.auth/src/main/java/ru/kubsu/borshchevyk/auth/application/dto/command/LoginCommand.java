package ru.kubsu.borshchevyk.auth.application.dto.command;

import lombok.Builder;

@Builder
public record LoginCommand(
        String email,
        String passwordHash
) { }
