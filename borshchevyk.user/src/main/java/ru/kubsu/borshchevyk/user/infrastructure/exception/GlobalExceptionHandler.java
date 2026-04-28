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
 * Class documentation.
 *
 * @author Aleksey Timko
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ProblemDetail> handleAlreadyExists(UserAlreadyExistsException ex) {
        log.warn("User already exists: {}", ex.getMessage());
        return createProblemDetail(HttpStatus.CONFLICT, "User Already Exists", ex.getMessage(), ex.getCode().name());
    }

    @ExceptionHandler(IncorrectInputFormatException.class)
    public ResponseEntity<ProblemDetail> handleIncorrectFormat(IncorrectInputFormatException ex) {
        log.warn("Incorrect input format: {}", ex.getMessage());
        return createProblemDetail(HttpStatus.BAD_REQUEST, "Incorrect Input Format", ex.getMessage(), ex.getCode().name());
    }

    @ExceptionHandler(UserForbiddenException.class)
    public ResponseEntity<ProblemDetail> handleForbidden(UserForbiddenException ex) {
        log.warn("User forbidden action: {}", ex.getMessage());
        return createProblemDetail(HttpStatus.FORBIDDEN, "Forbidden", ex.getMessage(), ex.getCode().name());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleNotFound(UserNotFoundException ex) {
        log.warn("User not found: {}", ex.getMessage());
        return createProblemDetail(HttpStatus.NOT_FOUND, "User Not Found", ex.getMessage(), ex.getCode().name());
    }

    @ExceptionHandler(UserServiceException.class)
    public ResponseEntity<ProblemDetail> handleUserServiceException(UserServiceException ex) {
        log.error("User service exception: {}", ex.getMessage());
        return createProblemDetail(HttpStatus.BAD_REQUEST, "User Service Error", ex.getMessage(), ex.getCode().name());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGeneralException(Exception ex) {
        log.error("Unexpected error occurred", ex);
        return createProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex.getMessage(), "INTERNAL_SERVER_ERROR");
    }

    private ResponseEntity<ProblemDetail> createProblemDetail(HttpStatus status, String title, String detail, String errorCode) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setTitle(title);
        problemDetail.setProperty("errorCode", errorCode);
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setType(URI.create("about:blank"));
        return ResponseEntity.status(status).body(problemDetail);
    }
}

