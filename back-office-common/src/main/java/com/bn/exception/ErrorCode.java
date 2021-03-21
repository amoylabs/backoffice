package com.bn.exception;

public interface ErrorCode {
    String SERVER_ERROR = "SERVER_ERROR";
    String VALIDATION_ERROR = "VALIDATION_ERROR";

    String getErrorCode();

    default ErrorSeverity getSeverity() {
        return ErrorSeverity.ERROR;
    }
}
