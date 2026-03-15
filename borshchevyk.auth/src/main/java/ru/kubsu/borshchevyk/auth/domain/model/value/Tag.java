package ru.kubsu.borshchevyk.auth.domain.model.value;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.kubsu.borshchevyk.auth.domain.exception.IncorrectInputFormatException;

/**
 * Value object representing a user tag.
 *
 * @author Aleksey Timko
 * @since 2026-03-14
 */
@Slf4j
@Getter
public class Tag {
    private final String value;

    /**
     * Constructs a new Tag.
     *
     * @param tag the tag value string
     * @throws IncorrectInputFormatException if the tag format is invalid
     */
    public Tag(String tag) {
        if (!tag.matches("^[a-zA-Z0-9_-]{3,16}$")) {
            log.debug("Invalid tag format: {}", tag);
            throw new IncorrectInputFormatException(IncorrectInputFormatException.InputFormat.TAG);
        }
        this.value = tag;
    }
}
