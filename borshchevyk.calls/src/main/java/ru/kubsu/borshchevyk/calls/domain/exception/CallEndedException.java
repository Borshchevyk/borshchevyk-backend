package ru.kubsu.borshchevyk.calls.domain.exception;

public class CallEndedException extends CallServiceException {
    public CallEndedException() {
        super(ErrorCode.CALL_ENDED);
    }
}
