package ru.kubsu.borshchevyk.user.infrastructure.adapter.in.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * Class documentation.
 *
 * @author Aleksey Timko
 */
public record AddContactRequest(
    @NotBlank
    @Schema(description = "User ID of the contact to add", example = "550e8400-e29b-41d4-a716-446655440000")
    String targetUserId,
    @NotBlank
    @Schema(description = "First name of the contact", example = "Ivan")
    String firstName,
    @Schema(description = "Last name of the contact", example = "Ivanov")
    String lastName
) {}

