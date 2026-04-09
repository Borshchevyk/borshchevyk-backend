package ru.kubsu.borshchevyk.user.infrastructure.adapter.in.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.kubsu.borshchevyk.user.domain.model.privacy.Visibility;

public record UpdatePrivacySettingsRequest(
    @Schema(description = "Visibility of user's email", example = "NOBODY")
    Visibility emailVisibility,
    @Schema(description = "Visibility for searching by email", example = "EVERYONE")
    Visibility searchByEmailVisibility,
    @Schema(description = "Visibility of user's profile photo", example = "EVERYONE")
    Visibility profilePhotoVisibility,
    @Schema(description = "Visibility for inviting to chat", example = "CONTACTS")
    Visibility inviteToChatVisibility
) {}
