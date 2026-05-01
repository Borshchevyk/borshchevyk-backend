package ru.kubsu.borshchevyk.auth.application.dto.command;

import lombok.Builder;

/**
 * Command for refreshing an authentication token.
 *
 * @param refreshToken the current refresh token
 * @author Aleksey Timko
 */
@Builder
public record RefreshCommand(String refreshToken) {
}
