package ru.kubsu.borshchevyk.message.domain.exception;

/**
 * Exception thrown when an operation requires a user to be a member of a chat, but they are not.
 *
 * @author Aleksey Timko
 */
public class UserNotInChatException extends MessageDomainException {
    public UserNotInChatException(String message) {
        super(message);
    }
}
