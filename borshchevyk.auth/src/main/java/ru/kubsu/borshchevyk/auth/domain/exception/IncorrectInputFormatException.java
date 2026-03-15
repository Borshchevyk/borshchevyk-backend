package ru.kubsu.borshchevyk.auth.domain.exception;

public class IncorrectInputFormatException extends AuthServiceException {
    public IncorrectInputFormatException(InputFormat inputFormat) {
        super(ErrorCode.INCORRECT_FORMAT, String.format("Incorrect %s format.", inputFormat.name().toLowerCase()));
    }

    public enum InputFormat {
        EMAIL,
        TAG
    }
}
