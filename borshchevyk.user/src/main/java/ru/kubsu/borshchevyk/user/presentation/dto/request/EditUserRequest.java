package ru.kubsu.borshchevyk.user.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Data Transfer Object for editing a user's profile.
 * Contains fields that can be updated.
 *
 * @author Aleksey Timko
 * @since 2026-03-15
 */
@Data
@Schema(description = "Request object for updating user profile data")
public class EditUserRequest {
    
    @Schema(description = "New email address", example = "new.email@example.com")
    private String email;
    
    @Schema(description = "New unique user tag", example = "super_user_2026")
    private String tag;
}
