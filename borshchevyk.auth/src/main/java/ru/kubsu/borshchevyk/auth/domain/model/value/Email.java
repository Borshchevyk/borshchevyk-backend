package ru.kubsu.borshchevyk.auth.domain.model.value;

import ru.kubsu.borshchevyk.auth.domain.exception.IncorrectInputFormatException;

/**
 * Value object representing an email address.
 *
 * @param value the email address string
 */
public record Email(String value) {
    /**
     * Constructs a new Email and validates format.
     *
     * @param value the email address string
     * @throws IncorrectInputFormatException if the email format is invalid
     */
    public Email {
        if (value == null || !value.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            throw new IncorrectInputFormatException(IncorrectInputFormatException.InputFormat.EMAIL);
        }
    }
}