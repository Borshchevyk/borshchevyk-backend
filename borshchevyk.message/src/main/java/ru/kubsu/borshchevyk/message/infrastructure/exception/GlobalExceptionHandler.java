package ru.kubsu.borshchevyk.message.infrastructure.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.kubsu.borshchevyk.message.domain.exception.*;

import java.time.LocalDateTime;

/**
 * Global exception handler for the message service.
 *
 * @author Aleksey Timko
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ChatNotFoundException.class)
    public ResponseEntity<MessageErrorResponse> handleNotFound(ChatNotFoundException ex) {
        log.warn("Chat not found: {}", ex.getMessage());
        return MessageErrorResponse.buildResponse(HttpStatus.NOT_FOUND, ex);
    }

    @ExceptionHandler(MessageNotFoundException.class)
    public ResponseEntity<MessageErrorResponse> handleMessageNotFound(MessageNotFoundException ex) {
        log.warn("Message not found: {}", ex.getMessage());
        return MessageErrorResponse.buildResponse(HttpStatus.NOT_FOUND, ex);
    }

    @ExceptionHandler(ForbiddenActionException.class)
    public ResponseEntity<MessageErrorResponse> handleForbidden(ForbiddenActionException ex) {
        log.warn("Forbidden action: {}", ex.getMessage());
        return MessageErrorResponse.buildResponse(HttpStatus.FORBIDDEN, ex);
    }

    @ExceptionHandler(UserNotInChatException.class)
    public ResponseEntity<MessageErrorResponse> handleUserNotInChat(UserNotInChatException ex) {
        log.warn("User not in chat: {}", ex.getMessage());
        return MessageErrorResponse.buildResponse(HttpStatus.FORBIDDEN, ex);
    }

    @ExceptionHandler(MessagingSerializationException.class)
    public ResponseEntity<MessageErrorResponse> handleSerializationException(MessagingSerializationException ex) {
        log.error("Messaging serialization exception", ex);
        return MessageErrorResponse.buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<MessageErrorResponse> handleGeneralException(Exception ex) {
        log.error("Unexpected error occurred", ex);
        MessageErrorResponse response = new MessageErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_SERVER_ERROR",
                ex.getMessage()
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
