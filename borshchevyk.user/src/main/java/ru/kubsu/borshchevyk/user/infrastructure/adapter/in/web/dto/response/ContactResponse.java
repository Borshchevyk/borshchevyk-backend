package ru.kubsu.borshchevyk.user.infrastructure.adapter.in.web.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

public record ContactResponse(
    @Schema(description = "Contact record ID", example = "110e8400-e29b-41d4-a716-446655440000")
    String id,
    @Schema(description = "User ID of the owner of the contact list", example = "220e8400-e29b-41d4-a716-446655440000")
    String ownerId,
    @Schema(description = "User ID of the contact", example = "550e8400-e29b-41d4-a716-446655440000")
    String contactUserId,
    @Schema(description = "First name of the contact", example = "Ivan")
    String contactFirstName,
    @Schema(description = "Last name of the contact", example = "Ivanov")
    String contactLastName,
    @Schema(description = "Timestamp when the contact was added")
    LocalDateTime addedAt
) {}
