package ru.kubsu.borshchevyk.auth.domain.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

/**
 * Standard authentication error response for consistent error reporting.
 *
 * @author Aleksey Timko
 * @since 2026-03-14
 * @param timestamp the exact time when the error occurred
 * @param status the HTTP status code
 * @param errorCode the internal business error code
 * @param message the human-readable explanation of the error
 */
@Slf4j
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
    /**
     * Constructs a {@link ResponseEntity} containing an {@code AuthErrorResponse}.
     *
     * @param status the HTTP status to be returned
     * @param ex the exception that caused the error
     * @return a {@code ResponseEntity} wrapping the error response
     */
    public static ResponseEntity<AuthErrorResponse> buildResponse(HttpStatus status, AuthServiceException ex) {
        log.error("Building error response: code={}, message={}", ex.getCode(), ex.getMessage());
        AuthErrorResponse response = new AuthErrorResponse(
                LocalDateTime.now(),
                status.value(),
                ex.getCode().name(),
                ex.getMessage()
        );
        return new ResponseEntity<>(response, status);
    }
}
