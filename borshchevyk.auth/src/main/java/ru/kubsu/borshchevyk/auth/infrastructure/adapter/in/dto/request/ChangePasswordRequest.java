package ru.kubsu.borshchevyk.auth.infrastructure.adapter.in.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * Request to change user password.
 *
 * @author Aleksey Timko
 * @since 2026-03-14
 */
@Data
@Schema(description = "Request to change user password")
@Slf4j
public class ChangePasswordRequest {
    @Schema(description = "User's email address", example = "user@example.com")
    private String email;

    @Schema(description = "User's old password hash", example = "e3b0c442...")
    private String oldPassword;

    @Schema(description = "User's new password hash", example = "e3b0c442...")
    private String newPassword;
}
