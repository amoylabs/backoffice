package com.bn.authorization;

import com.bn.exception.ErrorCode;
import com.bn.exception.ErrorSeverity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class AuthorizationException extends RuntimeException implements ErrorCode {
    public AuthorizationException(String message) {
        super(message);
    }

    @Override
    public String getErrorCode() {
        return "AUTHORIZATION_EXCEPTION";
    }

    @Override
    public ErrorSeverity getSeverity() {
        return ErrorSeverity.WARN;
    }
}
