package ru.kubsu.borshchevyk.auth.domain.model.value;

import ru.kubsu.borshchevyk.auth.domain.exception.IncorrectInputFormatException;

/**
 * Value object representing a user tag.
 *
 * @param value the tag value string
 */
public record Tag(String value) {
    /**
     * Constructs a new Tag and validates format.
     *
     * @param value the tag value string
     * @throws IncorrectInputFormatException if the tag format is invalid
     */
    public Tag {
        if (value == null || !value.matches("^[a-zA-Z0-9_-]{3,16}$")) {
            throw new IncorrectInputFormatException(IncorrectInputFormatException.InputFormat.TAG);
        }
    }
}