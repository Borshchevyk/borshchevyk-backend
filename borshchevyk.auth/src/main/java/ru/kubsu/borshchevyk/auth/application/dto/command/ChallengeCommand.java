package ru.kubsu.borshchevyk.auth.application.dto.command;

import lombok.Builder;

import java.util.UUID;

/**
 * Command for requesting a cryptographic challenge.
 *
 * @param userId the ID of the user requesting the challenge
 * @author Aleksey Timko
 * @since 2026-03-14
 */
@Builder
public record ChallengeCommand(UUID userId) { }
