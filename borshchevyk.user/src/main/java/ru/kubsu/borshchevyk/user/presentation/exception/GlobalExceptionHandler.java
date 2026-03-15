package ru.kubsu.borshchevyk.user.presentation.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.kubsu.borshchevyk.user.domain.exception.UserAlreadyExistsException;
import ru.kubsu.borshchevyk.user.domain.exception.UserErrorResponse;
import ru.kubsu.borshchevyk.user.domain.exception.UserNotFoundException;
import ru.kubsu.borshchevyk.user.domain.exception.UserServiceException;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<UserErrorResponse> handleNotFound(UserNotFoundException ex) {
        return UserErrorResponse.buildResponse(HttpStatus.NOT_FOUND, ex);
    }

    @ExceptionHandler(ru.kubsu.borshchevyk.user.domain.exception.UserForbiddenException.class)
    public ResponseEntity<UserErrorResponse> handleForbidden(ru.kubsu.borshchevyk.user.domain.exception.UserForbiddenException ex) {
        return UserErrorResponse.buildResponse(HttpStatus.FORBIDDEN, ex);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<UserErrorResponse> handleAlreadyExists(UserAlreadyExistsException ex) {
        return UserErrorResponse.buildResponse(HttpStatus.CONFLICT, ex);
    }

    @ExceptionHandler(UserServiceException.class)
    public ResponseEntity<UserErrorResponse> handleUserServiceException(UserServiceException ex) {
        return UserErrorResponse.buildResponse(HttpStatus.BAD_REQUEST, ex);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<UserErrorResponse> handleGeneralException(Exception ex) {
        UserErrorResponse response = new UserErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_SERVER_ERROR",
                ex.getMessage()
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
