package ru.kubsu.borshchevyk.sync.infrastructure.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.kubsu.borshchevyk.sync.domain.exception.SyncException;

import java.time.LocalDateTime;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SyncException.class)
    public ResponseEntity<SyncErrorResponse> handleSyncException(SyncException ex) {
        log.error("Sync exception: {}", ex.getMessage());
        return SyncErrorResponse.buildResponse(HttpStatus.BAD_REQUEST, ex);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<SyncErrorResponse> handleGeneralException(Exception ex) {
        log.error("Unexpected error occurred", ex);
        SyncErrorResponse response = new SyncErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_SERVER_ERROR",
                ex.getMessage()
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
