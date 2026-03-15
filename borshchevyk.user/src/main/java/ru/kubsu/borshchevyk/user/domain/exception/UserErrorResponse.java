package ru.kubsu.borshchevyk.user.domain.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

/**
 * Standard data carrier for error responses returned by the User service.
 *
 * @param timestamp the exact moment the error occurred
 * @param status    the HTTP status code
 * @param errorCode the internal business error code
 * @param message   the descriptive error message
 * @author Aleksey Timko
 * @since 2026-03-15
 */
@Slf4j
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
    /**
     * Factory method for creating a ResponseEntity with a UserErrorResponse.
     *
     * @param status the HTTP status to return
     * @param ex     the source business exception
     * @return a wrapped error response entity
     */
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
