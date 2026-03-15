package ru.kubsu.borshchevyk.user.presentation.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.kubsu.borshchevyk.user.domain.exception.UserAlreadyExistsException;
import ru.kubsu.borshchevyk.user.domain.exception.UserErrorResponse;
import ru.kubsu.borshchevyk.user.domain.exception.UserNotFoundException;
import ru.kubsu.borshchevyk.user.domain.exception.UserServiceException;

import java.time.LocalDateTime;

/**
 * Global exception handler for the user microservice.
 * Intercepts domain and system exceptions to return standardized error responses.
 *
 * @author Aleksey Timko
 * @since 2026-03-15
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles UserNotFoundException.
     *
     * @param ex the exception
     * @return 404 Not Found response
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<UserErrorResponse> handleNotFound(UserNotFoundException ex) {
        log.error("User not found exception: {}", ex.getMessage());
        return UserErrorResponse.buildResponse(HttpStatus.NOT_FOUND, ex);
    }

    /**
     * Handles UserForbiddenException.
     *
     * @param ex the exception
     * @return 403 Forbidden response
     */
    @ExceptionHandler(ru.kubsu.borshchevyk.user.domain.exception.UserForbiddenException.class)
    public ResponseEntity<UserErrorResponse> handleForbidden(ru.kubsu.borshchevyk.user.domain.exception.UserForbiddenException ex) {
        log.warn("Access denied exception: {}", ex.getMessage());
        return UserErrorResponse.buildResponse(HttpStatus.FORBIDDEN, ex);
    }

    /**
     * Handles UserAlreadyExistsException.
     *
     * @param ex the exception
     * @return 409 Conflict response
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<UserErrorResponse> handleAlreadyExists(UserAlreadyExistsException ex) {
        log.warn("User already exists: {}", ex.getMessage());
        return UserErrorResponse.buildResponse(HttpStatus.CONFLICT, ex);
    }

    /**
     * Handles general UserServiceException.
     *
     * @param ex the exception
     * @return 400 Bad Request response
     */
    @ExceptionHandler(UserServiceException.class)
    public ResponseEntity<UserErrorResponse> handleUserServiceException(UserServiceException ex) {
        log.error("User service exception: {}", ex.getMessage());
        return UserErrorResponse.buildResponse(HttpStatus.BAD_REQUEST, ex);
    }

    /**
     * Handles any unexpected exceptions.
     *
     * @param ex the exception
     * @return 500 Internal Server Error response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<UserErrorResponse> handleGeneralException(Exception ex) {
        log.error("Unexpected error occurred: ", ex);
        UserErrorResponse response = new UserErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_SERVER_ERROR",
                ex.getMessage()
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
