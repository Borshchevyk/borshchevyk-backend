package ru.kubsu.borshchevyk.user.application.dto.command;

import lombok.Builder;

@Builder
public record EditUserCommand(String userId, String email, String tag) { }
