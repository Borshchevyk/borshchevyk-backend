package ru.kubsu.borshchevyk.message.domain.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.time.Instant;

@Getter
public abstract class MessageServiceException extends RuntimeException {

    private final ErrorCode code;
    private final Object[] args;
    private final Instant instant = Instant.now();

    public MessageServiceException(ErrorCode code, Object... args) {
        super(code.name());
        this.code = code;
        this.args = args;
    }

    public static MessageServiceException getDefaultException() {
        return new MessageServiceException(ErrorCode.SERVER_ERROR) {};
    }

    public record ErrorResponse(
            MessageServiceException.ErrorCode code,
            String message,
            @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
            Instant timestamp
    ) { }

    public enum ErrorCode {
        SERVER_ERROR;

        @Override
        public String toString() {
            return this.name().toUpperCase();
        }
    }
}