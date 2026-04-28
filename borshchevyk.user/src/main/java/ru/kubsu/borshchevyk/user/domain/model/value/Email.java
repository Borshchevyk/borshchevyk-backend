package ru.kubsu.borshchevyk.user.domain.model.value;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.kubsu.borshchevyk.user.domain.exception.IncorrectInputFormatException;

/**
 * Value object representing a validated email address.
 *
 * @author Aleksey Timko
 * @since 2026-03-15
 */
@Slf4j
@Getter
public class Email {
    /**
     * The raw string value of the email.
     */
    private final String value;

    /**
     * Constructs and validates an Email object.
     *
     * @param email the raw email string to validate
     * @throws IncorrectInputFormatException if the email format is invalid
     */
    public Email(String email) {
        if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            log.error("Invalid email format attempted");
            throw new IncorrectInputFormatException(IncorrectInputFormatException.InputFormat.EMAIL);
        }
        this.value = email;
    }
}
