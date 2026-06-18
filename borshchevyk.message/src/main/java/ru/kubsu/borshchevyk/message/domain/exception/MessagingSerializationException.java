package ru.kubsu.borshchevyk.message.domain.exception;

public class MessagingSerializationException extends RuntimeException {
    public MessagingSerializationException(String message, Throwable cause) {
        super(message, cause);
    }
}