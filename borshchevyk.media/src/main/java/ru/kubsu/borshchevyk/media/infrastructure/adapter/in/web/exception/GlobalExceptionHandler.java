package ru.kubsu.borshchevyk.media.infrastructure.adapter.in.web.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.kubsu.borshchevyk.media.domain.exception.AttachmentNotFoundException;
import ru.kubsu.borshchevyk.media.domain.exception.ForbiddenActionException;
import ru.kubsu.borshchevyk.media.domain.exception.InvalidAttachmentTypeException;

import ru.kubsu.borshchevyk.media.domain.exception.StorageException;

import java.net.URI;
import java.util.stream.Collectors;

/**
 * Global exception handler for the media service.
 * Translates domain exceptions into RFC 7807 ProblemDetail responses.
 *
 * @author Aleksey Timko
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(StorageException.class)
    public ProblemDetail handleStorageException(StorageException ex) {
        log.error("Storage error: {}", ex.getMessage());
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Storage operation failed");
        problemDetail.setTitle("Storage Error");
        problemDetail.setType(URI.create("https://api.borshchevyk.kubsu.ru/errors/internal-server-error"));
        return problemDetail;
    }

    @ExceptionHandler(AttachmentNotFoundException.class)
    public ProblemDetail handleAttachmentNotFound(AttachmentNotFoundException ex) {
        log.warn("Attachment not found: {}", ex.getMessage());
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Attachment Not Found");
        problemDetail.setType(URI.create("https://api.borshchevyk.kubsu.ru/errors/not-found"));
        return problemDetail;
    }

    @ExceptionHandler(InvalidAttachmentTypeException.class)
    public ProblemDetail handleInvalidAttachmentType(InvalidAttachmentTypeException ex) {
        log.warn("Invalid attachment type: {}", ex.getMessage());
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setTitle("Invalid Attachment Type");
        problemDetail.setType(URI.create("https://api.borshchevyk.kubsu.ru/errors/bad-request"));
        return problemDetail;
    }

    @ExceptionHandler(ForbiddenActionException.class)
    public ProblemDetail handleForbiddenAction(ForbiddenActionException ex) {
        log.warn("Forbidden action: {}", ex.getMessage());
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
        problemDetail.setTitle("Forbidden Action");
        problemDetail.setType(URI.create("https://api.borshchevyk.kubsu.ru/errors/forbidden"));
        return problemDetail;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Illegal argument: {}", ex.getMessage());
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setTitle("Bad Request");
        problemDetail.setType(URI.create("https://api.borshchevyk.kubsu.ru/errors/bad-request"));
        return problemDetail;
    }

    @ExceptionHandler(IllegalStateException.class)
    public ProblemDetail handleIllegalState(IllegalStateException ex) {
        log.warn("Illegal state: {}", ex.getMessage());
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problemDetail.setTitle("Conflict");
        problemDetail.setType(URI.create("https://api.borshchevyk.kubsu.ru/errors/conflict"));
        return problemDetail;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        log.warn("Validation error: {}", errorMessage);
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, errorMessage);
        problemDetail.setTitle("Validation Failed");
        problemDetail.setType(URI.create("https://api.borshchevyk.kubsu.ru/errors/validation-failed"));
        return problemDetail;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneralException(Exception ex) {
        log.error("Internal server error", ex);
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
        problemDetail.setTitle("Internal Server Error");
        problemDetail.setType(URI.create("https://api.borshchevyk.kubsu.ru/errors/internal-server-error"));
        return problemDetail;
    }
}
