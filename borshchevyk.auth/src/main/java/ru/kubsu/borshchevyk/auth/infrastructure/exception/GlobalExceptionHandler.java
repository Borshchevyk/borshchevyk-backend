package ru.kubsu.borshchevyk.auth.infrastructure.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.kubsu.borshchevyk.auth.domain.exception.AuthErrorResponse;
import ru.kubsu.borshchevyk.auth.domain.exception.AuthServiceException;
import ru.kubsu.borshchevyk.auth.domain.exception.InvalidCredentialsException;
import ru.kubsu.borshchevyk.auth.domain.exception.UserAlreadyExistsException;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<AuthErrorResponse> handleAlreadyExists(UserAlreadyExistsException ex) {
        return AuthErrorResponse.buildResponse(HttpStatus.CONFLICT, ex);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<AuthErrorResponse> handleInvalidCredentials(InvalidCredentialsException ex) {
        return AuthErrorResponse.buildResponse(HttpStatus.UNAUTHORIZED, ex);
    }

    @ExceptionHandler(AuthServiceException.class)
    public ResponseEntity<AuthErrorResponse> handleAuthServiceException(AuthServiceException ex) {
        return AuthErrorResponse.buildResponse(HttpStatus.BAD_REQUEST, ex);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<AuthErrorResponse> handleGeneralException(Exception ex) {
        AuthErrorResponse response = new AuthErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_SERVER_ERROR",
                ex.getMessage()
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
