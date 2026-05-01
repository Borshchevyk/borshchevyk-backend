package ru.kubsu.borshchevyk.media.domain.exception;

/**
 * Exception thrown when an invalid attachment type is provided for a specific operation.
 *
 * @author Aleksey Timko
 */
public class InvalidAttachmentTypeException extends RuntimeException {
    public InvalidAttachmentTypeException(String message) {
        super(message);
    }
}
