package ru.kubsu.borshchevyk.auth.domain.model.result;

import lombok.Builder;

import java.util.UUID;

@Builder
public record RegisterResult(UUID userId) { }
