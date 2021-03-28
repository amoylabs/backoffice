package com.bn.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ConflictException extends RuntimeException implements ErrorCode {
    public ConflictException(String message) {
        super(message);
    }

    @Override
    public String getErrorCode() {
        return "CONFLICT";
    }

    @Override
    public ErrorSeverity getSeverity() {
        return ErrorSeverity.WARN;
    }
}
