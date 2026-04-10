package ru.kubsu.borshchevyk.sync.infrastructure.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

@Slf4j
@Schema(description = "Standard sync service error response")
public record SyncErrorResponse(
        @Schema(description = "Timestamp of the error", example = "2026-03-15T15:30:00")
        LocalDateTime timestamp,
        @Schema(description = "HTTP status code", example = "400")
        int status,
        @Schema(description = "Specific error code", example = "SyncException")
        String errorCode,
        @Schema(description = "Human-readable error message", example = "Sync failed.")
        String message
) {
    public static ResponseEntity<SyncErrorResponse> buildResponse(HttpStatus status, Exception ex) {
        log.error("Building error response: code={}, message={}", ex.getClass().getSimpleName(), ex.getMessage());
        SyncErrorResponse response = new SyncErrorResponse(
                LocalDateTime.now(),
                status.value(),
                ex.getClass().getSimpleName(),
                ex.getMessage()
        );
        return new ResponseEntity<>(response, status);
    }
}
