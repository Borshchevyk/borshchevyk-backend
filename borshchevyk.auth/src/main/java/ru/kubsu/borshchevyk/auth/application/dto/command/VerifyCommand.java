package ru.kubsu.borshchevyk.auth.application.dto.command;

import lombok.Builder;

import java.util.UUID;

/**
 * Command for verifying a cryptographic challenge.
 *
 * @param userId    the ID of the user verifying the challenge
 * @param signature the cryptographic signature of the challenge
 * @author Aleksey Timko
 * @since 2026-03-14
 */
@Builder
public record VerifyCommand(
        UUID userId,
        String signature
) { }
