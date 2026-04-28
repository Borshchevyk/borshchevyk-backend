package ru.kubsu.borshchevyk.auth.infrastructure.adapter.in.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Request to register a new user.
 *
 * @param email               User's email address
 * @param tag                 Unique user tag
 * @param firstName           User's first name
 * @param lastName            User's last name
 * @param passwordHash        Client-side AuthHash (not the raw password)
 * @param publicKey           Base64 encoded RSA Public Key
 * @param encryptedPrivateKey Base64 encoded encrypted Private Key
 * @author Aleksey Timko
 * @since 2026-03-14
 */
@Schema(description = "Request to register a new user")
public record RegisterRequest(
        @Email(message = "Invalid email format")
        @NotBlank(message = "Email is mandatory")
        @Schema(description = "User's email address", example = "user@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
        String email,
        @NotBlank(message = "Tag is mandatory")
        @Schema(description = "Unique user tag", example = "john_doe", requiredMode = Schema.RequiredMode.REQUIRED)
        String tag,
        @NotBlank(message = "First name is mandatory")
        @Schema(description = "User's first name", example = "John", requiredMode = Schema.RequiredMode.REQUIRED)
        String firstName,
        @Schema(description = "User's last name (optional)", example = "Doe")
        String lastName,
        @NotBlank(message = "Password hash is mandatory")
        @Schema(description = "Client-side AuthHash (not the raw password)", example = "e3b0c442...", requiredMode = Schema.RequiredMode.REQUIRED)
        String passwordHash,
        @NotBlank(message = "Public key is mandatory")
        @Schema(description = "Base64 encoded RSA Public Key", example = "MIIBIjAN...", requiredMode = Schema.RequiredMode.REQUIRED)
        String publicKey,
        @NotBlank(message = "Encrypted private key is mandatory")
        @Schema(description = "Base64 encoded encrypted Private Key", example = "v2...", requiredMode = Schema.RequiredMode.REQUIRED)
        String encryptedPrivateKey
) { }
