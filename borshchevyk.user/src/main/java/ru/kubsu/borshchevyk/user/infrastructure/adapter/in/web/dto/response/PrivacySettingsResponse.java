package ru.kubsu.borshchevyk.user.infrastructure.adapter.in.web.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.kubsu.borshchevyk.user.domain.model.privacy.Visibility;

/**
 * Class documentation.
 *
 * @author Aleksey Timko
 */
public record PrivacySettingsResponse(
    @Schema(description = "Unique identifier of the user", example = "550e8400-e29b-41d4-a716-446655440000")
    String userId,
    @Schema(description = "Visibility of user's email", example = "NOBODY")
    Visibility emailVisibility,
    @Schema(description = "Visibility for searching by email", example = "EVERYONE")
    Visibility searchByEmailVisibility,
    @Schema(description = "Visibility of user's profile photo", example = "EVERYONE")
    Visibility profilePhotoVisibility,
    @Schema(description = "Visibility for inviting to chat", example = "CONTACTS")
    Visibility inviteToChatVisibility
) {}

