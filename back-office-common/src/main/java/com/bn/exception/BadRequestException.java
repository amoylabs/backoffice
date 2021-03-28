package com.bn.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException implements ErrorCode {
    public BadRequestException(String message) {
        super(message);
    }

    @Override
    public String getErrorCode() {
        return "BAD_REQUEST";
    }

    @Override
    public ErrorSeverity getSeverity() {
        return ErrorSeverity.WARN;
    }
}
