package ru.kubsu.borshchevyk.auth.domain.model.result;

import lombok.Builder;

/**
 * Result of a successful verification or login process, containing JWT tokens.
 *
 * @param accessToken the JWT used for authentication
 * @param refreshToken the JWT used to refresh the session
 * @author Aleksey Timko
 */
@Builder
public record VerifyResult(
        String accessToken,
        String refreshToken
) {}