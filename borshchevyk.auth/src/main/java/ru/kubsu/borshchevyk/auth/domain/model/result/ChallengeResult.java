package ru.kubsu.borshchevyk.auth.domain.model.result;

import lombok.Builder;

/**
 * Result of a challenge generation request.
 *
 * @param challenge The generated challenge string for the user to sign.
 */
@Builder
public record ChallengeResult(String challenge) { }