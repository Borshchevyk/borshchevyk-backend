package ru.kubsu.borshchevyk.auth.infrastructure.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.kubsu.borshchevyk.auth.domain.exception.AuthServiceException;
import ru.kubsu.borshchevyk.auth.domain.exception.IncorrectInputFormatException;
import ru.kubsu.borshchevyk.auth.domain.exception.InvalidCredentialsException;
import ru.kubsu.borshchevyk.auth.domain.exception.MessagingSerializationException;
import ru.kubsu.borshchevyk.auth.domain.exception.UserAlreadyExistsException;
import ru.kubsu.borshchevyk.auth.domain.exception.AccountNotFoundException;
import ru.kubsu.borshchevyk.auth.domain.exception.ChallengeExpiredException;
import ru.kubsu.borshchevyk.auth.domain.exception.InvalidSignatureException;

import java.time.Instant;

/**
 * Global exception handler for the authentication microservice.
 *
 * @author Aleksey Timko
 * @since 2026-03-14
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ProblemDetail handleAlreadyExists(UserAlreadyExistsException ex) {
        log.warn("User already exists: {}", ex.getMessage());
        return createProblemDetail(HttpStatus.CONFLICT, "User Already Exists", ex.getMessage());
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ProblemDetail handleInvalidCredentials(InvalidCredentialsException ex) {
        log.warn("Invalid credentials: {}", ex.getMessage());
        return createProblemDetail(HttpStatus.UNAUTHORIZED, "Invalid Credentials", ex.getMessage());
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ProblemDetail handleAccountNotFound(AccountNotFoundException ex) {
        log.warn("Account not found: {}", ex.getMessage());
        return createProblemDetail(HttpStatus.UNAUTHORIZED, "Account Not Found", ex.getMessage());
    }

    @ExceptionHandler(ChallengeExpiredException.class)
    public ProblemDetail handleChallengeExpired(ChallengeExpiredException ex) {
        log.warn("Challenge expired: {}", ex.getMessage());
        return createProblemDetail(HttpStatus.UNAUTHORIZED, "Challenge Expired", ex.getMessage());
    }

    @ExceptionHandler(InvalidSignatureException.class)
    public ProblemDetail handleInvalidSignature(InvalidSignatureException ex) {
        log.warn("Invalid signature: {}", ex.getMessage());
        return createProblemDetail(HttpStatus.UNAUTHORIZED, "Invalid Signature", ex.getMessage());
    }

    @ExceptionHandler(IncorrectInputFormatException.class)
    public ProblemDetail handleIncorrectInputFormat(IncorrectInputFormatException ex) {
        log.warn("Incorrect input format: {}", ex.getMessage());
        return createProblemDetail(HttpStatus.BAD_REQUEST, "Incorrect Input Format", ex.getMessage());
    }

    @ExceptionHandler(AuthServiceException.class)
    public ProblemDetail handleAuthServiceException(AuthServiceException ex) {
        log.error("Auth service exception: {}", ex.getMessage());
        return createProblemDetail(HttpStatus.BAD_REQUEST, "Auth Service Error", ex.getMessage());
    }

    @ExceptionHandler(MessagingSerializationException.class)
    public ProblemDetail handleMessagingSerializationException(MessagingSerializationException ex) {
        log.error("Messaging serialization exception", ex);
        return createProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Serialization Error", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneralException(Exception ex) {
        log.error("Unexpected error occurred", ex);
        return createProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "An unexpected error occurred");
    }

    private ProblemDetail createProblemDetail(HttpStatus status, String title, String detail) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setTitle(title);
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }
}
