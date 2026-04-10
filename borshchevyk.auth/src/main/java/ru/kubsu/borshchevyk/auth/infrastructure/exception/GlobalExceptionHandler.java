package ru.kubsu.borshchevyk.auth.infrastructure.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.kubsu.borshchevyk.auth.domain.exception.AuthErrorResponse;
import ru.kubsu.borshchevyk.auth.domain.exception.AuthServiceException;
import ru.kubsu.borshchevyk.auth.domain.exception.IncorrectInputFormatException;
import ru.kubsu.borshchevyk.auth.domain.exception.InvalidCredentialsException;
import ru.kubsu.borshchevyk.auth.domain.exception.MessagingSerializationException;
import ru.kubsu.borshchevyk.auth.domain.exception.UserAlreadyExistsException;

import java.time.LocalDateTime;

/**
 * Global exception handler for the authentication microservice.
 *
 * @author Aleksey Timko
 * @since 2026-03-14
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles {@link UserAlreadyExistsException}.
     *
     * @param ex the exception
     * @return the error response
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<AuthErrorResponse> handleAlreadyExists(UserAlreadyExistsException ex) {
        log.warn("User already exists: {}", ex.getMessage());
        return AuthErrorResponse.buildResponse(HttpStatus.CONFLICT, ex);
    }

    /**
     * Handles {@link InvalidCredentialsException}.
     *
     * @param ex the exception
     * @return the error response
     */
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<AuthErrorResponse> handleInvalidCredentials(InvalidCredentialsException ex) {
        log.warn("Invalid credentials: {}", ex.getMessage());
        return AuthErrorResponse.buildResponse(HttpStatus.UNAUTHORIZED, ex);
    }

    /**
     * Handles {@link IncorrectInputFormatException}.
     *
     * @param ex the exception
     * @return the error response
     */
    @ExceptionHandler(IncorrectInputFormatException.class)
    public ResponseEntity<AuthErrorResponse> handleIncorrectInputFormat(IncorrectInputFormatException ex) {
        log.warn("Incorrect input format: {}", ex.getMessage());
        return AuthErrorResponse.buildResponse(HttpStatus.BAD_REQUEST, ex);
    }

    /**
     * Handles {@link AuthServiceException}.
     *
     * @param ex the exception
     * @return the error response
     */
    @ExceptionHandler(AuthServiceException.class)
    public ResponseEntity<AuthErrorResponse> handleAuthServiceException(AuthServiceException ex) {
        log.error("Auth service exception: {}", ex.getMessage());
        return AuthErrorResponse.buildResponse(HttpStatus.BAD_REQUEST, ex);
    }

    /**
     * Handles {@link MessagingSerializationException}.
     *
     * @param ex the exception
     * @return the error response
     */
    @ExceptionHandler(MessagingSerializationException.class)
    public ResponseEntity<AuthErrorResponse> handleMessagingSerializationException(MessagingSerializationException ex) {
        log.error("Messaging serialization exception", ex);
        AuthErrorResponse response = new AuthErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_SERVER_ERROR",
                ex.getMessage()
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles all other exceptions.
     *
     * @param ex the exception
     * @return the error response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<AuthErrorResponse> handleGeneralException(Exception ex) {
        log.error("Unexpected error occurred", ex);
        AuthErrorResponse response = new AuthErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_SERVER_ERROR",
                ex.getMessage()
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
