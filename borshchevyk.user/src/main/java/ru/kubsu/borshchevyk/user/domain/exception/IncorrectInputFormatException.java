package ru.kubsu.borshchevyk.user.domain.exception;

public class IncorrectInputFormatException extends UserServiceException {
    public IncorrectInputFormatException(InputFormat inputFormat) {
        super(ErrorCode.INCORRECT_FORMAT, String.format("Incorrect %s format.", inputFormat.name().toLowerCase()));
    }

    public enum InputFormat {
        EMAIL,
        TAG
    }
}
