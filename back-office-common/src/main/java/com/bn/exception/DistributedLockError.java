package com.bn.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class DistributedLockError extends RuntimeException implements ErrorCode {
    public DistributedLockError(Throwable err) {
        super(err);
    }

    public DistributedLockError(String message, Throwable err) {
        super(message, err);
    }

    @Override
    public String getErrorCode() {
        return "DISTRIBUTED_LOCK_ERROR";
    }

    @Override
    public ErrorSeverity getSeverity() {
        return ErrorSeverity.ERROR;
    }
}