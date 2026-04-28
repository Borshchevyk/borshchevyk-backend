package ru.kubsu.borshchevyk.user.domain.model.value;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.kubsu.borshchevyk.user.domain.exception.IncorrectInputFormatException;

/**
 * Value object representing a validated user tag (username).
 *
 * @author Aleksey Timko
 * @since 2026-03-15
 */
@Slf4j
@Getter
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
            log.error("Invalid tag format attempted");
            throw new IncorrectInputFormatException(IncorrectInputFormatException.InputFormat.TAG);
        }
        this.value = tag;
    }
}
