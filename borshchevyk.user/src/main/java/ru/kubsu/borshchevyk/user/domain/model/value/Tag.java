package ru.kubsu.borshchevyk.user.domain.model.value;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import ru.kubsu.borshchevyk.user.domain.exception.IncorrectInputFormatException;

/**
 * Value object representing a validated user tag (username).
 *
 * @author Aleksey Timko
 * @since 2026-03-15
 */
@Getter
@EqualsAndHashCode
public class Tag {
    /**
     * The raw string value of the tag.
     */
    private final String value;

    /**
     * Constructs and validates a Tag object.
     *
     * @param tag the raw tag string to validate
     * @throws IncorrectInputFormatException if the tag format is invalid
     */
    public Tag(String tag) {
        if (!tag.matches("^[a-zA-Z0-9_-]{3,16}$")) {
            throw new IncorrectInputFormatException(IncorrectInputFormatException.InputFormat.TAG);
        }
        this.value = tag;
    }
}
