package ru.kubsu.borshchevyk.auth.domain.exception;

/**
 * Exception thrown when the format of an input (email or tag) is invalid.
 */
public class IncorrectInputFormatException extends AuthServiceException {
    /**
     * Constructs the exception based on the specific input type that failed validation.
     *
     * @param inputFormat the type of input (EMAIL or TAG) with invalid format
     */
    public IncorrectInputFormatException(InputFormat inputFormat) {
        super(ErrorCode.INCORRECT_FORMAT, String.format("Incorrect %s format.", inputFormat.name().toLowerCase()));
    }

    /**
     * Types of input fields subject to format validation.
     */
    public enum InputFormat {
        /**
         * Email address format.
         */
        EMAIL,
        /**
         * User tag/nickname format.
         */
        TAG
    }
}
