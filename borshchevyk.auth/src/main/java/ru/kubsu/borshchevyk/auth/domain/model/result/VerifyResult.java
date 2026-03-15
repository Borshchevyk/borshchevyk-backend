package ru.kubsu.borshchevyk.auth.domain.model.result;

import lombok.Builder;

@Builder
public record VerifyResult(
        String accessToken,
        String refreshToken
) { }
