package ru.kubsu.borshchevyk.auth.domain.model.value;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.kubsu.borshchevyk.auth.domain.exception.IncorrectInputFormatException;

/**
 * Value object representing an email address.
 *
 * @author Aleksey Timko
 * @since 2026-03-14
 */
@Slf4j
@Getter
public class Email {
    private final String value;

    /**
     * Constructs a new Email.
     *
     * @param email the email address string
     * @throws IncorrectInputFormatException if the email format is invalid
     */
    public Email(String email) {
        if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            log.debug("Invalid email format");
            throw new IncorrectInputFormatException(IncorrectInputFormatException.InputFormat.EMAIL);
        }
        this.value = email;
    }
}
