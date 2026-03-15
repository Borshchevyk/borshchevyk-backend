package ru.kubsu.borshchevyk.auth.domain.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

@Schema(description = "Standard authentication error response")
public record AuthErrorResponse(
        @Schema(description = "Timestamp of the error", example = "2026-03-15T15:30:00")
        LocalDateTime timestamp,
        @Schema(description = "HTTP status code", example = "400")
        int status,
        @Schema(description = "Specific error code", example = "INCORRECT_FORMAT")
        String errorCode,
        @Schema(description = "Human-readable error message", example = "Incorrect email format.")
        String message
) {
    public static ResponseEntity<AuthErrorResponse> buildResponse(HttpStatus status, AuthServiceException ex) {
        AuthErrorResponse response = new AuthErrorResponse(
                LocalDateTime.now(),
                status.value(),
                ex.getCode().name(),
                ex.getMessage()
        );
        return new ResponseEntity<>(response, status);
    }
}
