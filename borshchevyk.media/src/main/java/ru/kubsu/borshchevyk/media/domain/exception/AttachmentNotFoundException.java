package ru.kubsu.borshchevyk.media.domain.exception;

/**
 * Exception thrown when an attachment is not found.
 *
 * @author Aleksey Timko
 */
public class AttachmentNotFoundException extends RuntimeException {
    public AttachmentNotFoundException(String message) {
        super(message);
    }
}
