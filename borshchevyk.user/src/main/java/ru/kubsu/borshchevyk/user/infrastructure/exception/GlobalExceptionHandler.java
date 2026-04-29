package ru.kubsu.borshchevyk.user.infrastructure.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.kubsu.borshchevyk.user.domain.exception.*;

import java.net.URI;
import java.time.Instant;

/**
 * Global exception handler for the User service, providing standardized RFC 7807 error responses.
 *
 * @author Aleksey Timko
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ProblemDetail> handleAlreadyExists(UserAlreadyExistsException ex) {
        log.warn("User conflict: {}", ex.getMessage());
        return createProblemDetail(HttpStatus.CONFLICT, "User Already Exists", ex);
    }

    @ExceptionHandler(IncorrectInputFormatException.class)
    public ResponseEntity<ProblemDetail> handleIncorrectFormat(IncorrectInputFormatException ex) {
        log.warn("Input error: {}", ex.getMessage());
        return createProblemDetail(HttpStatus.BAD_REQUEST, "Incorrect Input Format", ex);
    }

    @ExceptionHandler(UserForbiddenException.class)
    public ResponseEntity<ProblemDetail> handleForbidden(UserForbiddenException ex) {
        log.warn("Access denied: {}", ex.getMessage());
        return createProblemDetail(HttpStatus.FORBIDDEN, "Forbidden", ex);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleNotFound(UserNotFoundException ex) {
        log.warn("User not found: {}", ex.getMessage());
        return createProblemDetail(HttpStatus.NOT_FOUND, "User Not Found", ex);
    }

    @ExceptionHandler(UserServiceException.class)
    public ResponseEntity<ProblemDetail> handleUserServiceException(UserServiceException ex) {
        log.error("Internal service error: {}", ex.getMessage());
        return createProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR, "User Service Error", ex);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGeneralException(Exception ex) {
        log.error("Unexpected error occurred", ex);
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.");
        problemDetail.setTitle("Internal Server Error");
        problemDetail.setProperty("errorCode", "INTERNAL_SERVER_ERROR");
        problemDetail.setProperty("timestamp", Instant.now());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
    }

    private ResponseEntity<ProblemDetail> createProblemDetail(HttpStatus status, String title, UserServiceException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, ex.getMessage());
        problemDetail.setTitle(title);
        problemDetail.setProperty("errorCode", ex.getCode().name());
        problemDetail.setProperty("timestamp", Instant.now());
        return ResponseEntity.status(status).body(problemDetail);
    }
}

