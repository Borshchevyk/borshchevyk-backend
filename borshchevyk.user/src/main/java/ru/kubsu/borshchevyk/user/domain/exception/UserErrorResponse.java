package ru.kubsu.borshchevyk.user.domain.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

@Schema(description = "Standard user management error response")
public record UserErrorResponse(
        @Schema(description = "Timestamp of the error", example = "2026-03-15T15:30:00")
        LocalDateTime timestamp,
        @Schema(description = "HTTP status code", example = "400")
        int status,
        @Schema(description = "Specific error code", example = "USER_NOT_FOUND")
        String errorCode,
        @Schema(description = "Human-readable error message", example = "User was not found.")
        String message
) {
    public static ResponseEntity<UserErrorResponse> buildResponse(HttpStatus status, UserServiceException ex) {
        UserErrorResponse response = new UserErrorResponse(
                LocalDateTime.now(),
                status.value(),
                ex.getCode().name(),
                ex.getMessage()
        );
        return new ResponseEntity<>(response, status);
    }
}
