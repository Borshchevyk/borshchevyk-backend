package ru.kubsu.borshchevyk.user.domain.model.privacy;

/**
 * Enumeration of possible visibility levels for privacy settings.
 *
 * @author Aleksey Timko
 */
public enum Visibility {
    /** Visible to everyone. */
    EVERYONE,
    /** Visible only to contacts. */
    CONTACTS,
    /** Visible to nobody (private). */
    NOBODY
}
