package ru.kubsu.borshchevyk.auth.application.dto.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Command for changing a user password.
 *
 * @author Aleksey Timko
 * @since 2026-03-14
 */
@Getter
@Builder
@AllArgsConstructor
@Slf4j
public class ChangePasswordCommand {
    private final String email;
    private final String oldPassword;
    private final String newPassword;

    /**
     * Logs the password change request.
     * Secrets are not logged.
     */
    public void logRequest() {
        log.info("Password change requested for user: {}", email);
    }
}
