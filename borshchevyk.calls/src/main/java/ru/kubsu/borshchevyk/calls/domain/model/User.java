package ru.kubsu.borshchevyk.calls.domain.model;

import lombok.Builder;

import java.util.UUID;

@Builder
public record User(
    UUID id,
    String firstName,
    String lastName,
    String tag,
    String avatarUrl
) {}
