package ru.kubsu.borshchevyk.sync.domain.exception;

/**
 * Base domain exception for Sync context.
 *
 * @author Aleksey Timko
 * @since 2026-03-01
 */
public class SyncException extends RuntimeException {
    public SyncException(String message) {
        super(message);
    }

    public SyncException(String message, Throwable cause) {
        super(message, cause);
    }
}
