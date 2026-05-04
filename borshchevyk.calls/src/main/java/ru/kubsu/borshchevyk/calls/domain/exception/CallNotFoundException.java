package ru.kubsu.borshchevyk.calls.domain.exception;

public class CallNotFoundException extends CallServiceException {
    public CallNotFoundException() {
        super(ErrorCode.CALL_NOT_FOUND);
    }
}
