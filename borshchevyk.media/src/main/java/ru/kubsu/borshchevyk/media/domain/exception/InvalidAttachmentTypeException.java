package ru.kubsu.borshchevyk.media.domain.exception;

public class InvalidAttachmentTypeException extends RuntimeException {
    public InvalidAttachmentTypeException(String message) {
        super(message);
    }
}
