package ru.kubsu.borshchevyk.user.domain.exception;

/**
 * Exception thrown when user input (like email or tag) does not match expected formatting rules.
 *
 * @author Aleksey Timko
 * @since 2026-03-15
 */
public class IncorrectInputFormatException extends UserServiceException {

    /**
     * Constructs a new IncorrectInputFormatException for the given input type.
     *
     * @param inputFormat the type of input that was incorrectly formatted
     */
    public IncorrectInputFormatException(InputFormat inputFormat) {
        super(ErrorCode.INCORRECT_FORMAT, String.format("Incorrect %s format.", inputFormat.name().toLowerCase()));
    }

    /**
     * Enumeration of fields that may have formatting errors.
     */
    public enum InputFormat {
        EMAIL,
        TAG
    }
}
