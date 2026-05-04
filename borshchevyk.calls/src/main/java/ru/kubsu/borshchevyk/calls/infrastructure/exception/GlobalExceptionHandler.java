package ru.kubsu.borshchevyk.calls.infrastructure.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.kubsu.borshchevyk.calls.domain.exception.CallServiceException;

import java.util.Locale;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(CallServiceException.class)
    public ResponseEntity<CallServiceException.ErrorResponse> handleAuthException(CallServiceException ex, Locale locale) {
        log.error("Auth error [{}]: {}", ex.getCode(), ex.getMessage());

        String translatedMessage = messageSource.getMessage(
                ex.getCode().toString(),
                ex.getArgs(),
                locale
        );

        CallServiceException.ErrorResponse body = new CallServiceException.ErrorResponse(
                ex.getCode(),
                translatedMessage,
                ex.getInstant()
        );

        return ResponseEntity.status(ex.getHttpStatus()).body(body);
    }

    @ExceptionHandler(Exception.class)
    public void handleGeneralException(Exception ex) {
        log.error("Unexpected error", ex);
        throw CallServiceException.getDefaultException();
    }
}