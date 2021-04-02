package com.bn.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException implements ErrorCode {
    public NotFoundException(String message) {
        super(message);
    }

    @Override
    public String getErrorCode() {
        return "RESOURCE_NOT_FOUND";
    }

    @Override
    public ErrorSeverity getSeverity() {
        return ErrorSeverity.WARN;
    }
}
