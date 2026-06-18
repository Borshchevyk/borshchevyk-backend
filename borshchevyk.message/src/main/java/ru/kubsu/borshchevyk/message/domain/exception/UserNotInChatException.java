package ru.kubsu.borshchevyk.message.domain.exception;

public class UserNotInChatException extends RuntimeException {
    public UserNotInChatException(String message) {
        super(message);
    }
}