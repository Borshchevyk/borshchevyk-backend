package ru.kubsu.borshchevyk.user.infrastructure.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.kubsu.borshchevyk.user.domain.exception.*;

import java.time.LocalDateTime;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<UserErrorResponse> handleAlreadyExists(UserAlreadyExistsException ex) {
        log.warn("User already exists: {}", ex.getMessage());
        return UserErrorResponse.buildResponse(HttpStatus.CONFLICT, ex);
    }

    @ExceptionHandler(IncorrectInputFormatException.class)
    public ResponseEntity<UserErrorResponse> handleIncorrectFormat(IncorrectInputFormatException ex) {
        log.warn("Incorrect input format: {}", ex.getMessage());
        return UserErrorResponse.buildResponse(HttpStatus.BAD_REQUEST, ex);
    }

    @ExceptionHandler(UserForbiddenException.class)
    public ResponseEntity<UserErrorResponse> handleForbidden(UserForbiddenException ex) {
        log.warn("User forbidden action: {}", ex.getMessage());
        return UserErrorResponse.buildResponse(HttpStatus.FORBIDDEN, ex);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<UserErrorResponse> handleNotFound(UserNotFoundException ex) {
        log.warn("User not found: {}", ex.getMessage());
        return UserErrorResponse.buildResponse(HttpStatus.NOT_FOUND, ex);
    }

    @ExceptionHandler(UserServiceException.class)
    public ResponseEntity<UserErrorResponse> handleUserServiceException(UserServiceException ex) {
        log.error("User service exception: {}", ex.getMessage());
        return UserErrorResponse.buildResponse(HttpStatus.BAD_REQUEST, ex);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<UserErrorResponse> handleGeneralException(Exception ex) {
        log.error("Unexpected error occurred", ex);
        UserErrorResponse response = new UserErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_SERVER_ERROR",
                ex.getMessage()
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
