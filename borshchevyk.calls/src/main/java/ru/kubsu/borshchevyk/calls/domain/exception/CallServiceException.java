package ru.kubsu.borshchevyk.calls.domain.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.api.Http;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.Instant;

@Getter
public abstract class CallServiceException extends RuntimeException {

    private final ErrorCode code;
    private final Object[] args;
    private final Instant instant = Instant.now();
    private final HttpStatus httpStatus;

    public CallServiceException(ErrorCode code, Object... args) {
        super(code.name());
        this.code = code;
        this.httpStatus = code.httpStatus;
        this.args = args;
    }

    public static CallServiceException getDefaultException() {
        return new CallServiceException(
                ErrorCode.SERVER_ERROR,
                HttpStatus.INTERNAL_SERVER_ERROR
        ) {};
    }

    public record ErrorResponse(
            CallServiceException.ErrorCode code,
            String message,
            @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
            Instant timestamp
    ) { }

    public enum ErrorCode {
        CALL_ENDED(HttpStatus.NOT_FOUND),
        USER_FORBIDDEN(HttpStatus.FORBIDDEN),
        CALL_NOT_FOUND(HttpStatus.NOT_FOUND),
        SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR);

        private final HttpStatus httpStatus;

        ErrorCode(HttpStatus httpStatus) {
            this.httpStatus = httpStatus;
        }

        @Override
        public String toString() {
            return this.name().toUpperCase();
        }
    }
}
