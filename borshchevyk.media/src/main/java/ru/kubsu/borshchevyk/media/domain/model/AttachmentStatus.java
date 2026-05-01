package ru.kubsu.borshchevyk.media.domain.model;

/**
 * Enum representing the lifecycle status of an attachment.
 *
 * @author Aleksey Timko
 */
public enum AttachmentStatus {
    INITIALIZED,
    UPLOADING,
    UPLOADED,
    PROCESSING,
    READY,
    FAILED,
    DELETED
}
