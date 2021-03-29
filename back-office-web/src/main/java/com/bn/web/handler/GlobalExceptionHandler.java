package com.bn.web.handler;

import com.bn.exception.ErrorCode;
import com.bn.exception.ErrorSeverity;
import com.bn.web.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler({Exception.class, Error.class})
    public final ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        if (ex instanceof ErrorCode) {
            ErrorCode errorCode = (ErrorCode) ex;
            if (ErrorSeverity.ERROR == errorCode.getSeverity()) {
                handleRequestInfoErrorLogging(ex, request);
            } else if (ErrorSeverity.WARN == errorCode.getSeverity()) {
                handleWarningRequestInfoLogging(ex, request);
            }

            ErrorResponse error = new ErrorResponse(errorCode.getErrorCode(), List.of(toSafeExceptionMessage(ex)));
            ResponseStatus status = ex.getClass().getAnnotation(ResponseStatus.class);
            return new ResponseEntity<>(error, status.value());
        }

        handleRequestInfoErrorLogging(ex, request);
        ErrorResponse error = new ErrorResponse(ErrorCode.SERVER_ERROR, List.of(toSafeExceptionMessage(ex)));
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public final ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex) {
        List<String> details = ex.getConstraintViolations().stream().map(ConstraintViolation::getMessage).collect(Collectors.toList());
        ErrorResponse apiError = new ErrorResponse(ErrorCode.VALIDATION_ERROR, details);
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @Override
    @NonNull
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<String> details = ex.getBindingResult().getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
        ErrorResponse error = new ErrorResponse(ErrorCode.VALIDATION_ERROR, details);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    private void handleRequestInfoErrorLogging(Exception ex, WebRequest webRequest) {
        if (webRequest instanceof ServletWebRequest) {
            ServletWebRequest servletWebRequest = (ServletWebRequest) webRequest;
            HttpServletRequest request = servletWebRequest.getRequest();
            log.error("{}:{} : {}", request.getMethod(), request.getRequestURI(), toSafeExceptionMessage(ex));
            log.error(toSafeExceptionMessage(ex), ex);
        }
    }

    private void handleWarningRequestInfoLogging(Exception ex, WebRequest webRequest) {
        if (webRequest instanceof ServletWebRequest) {
            ServletWebRequest servletWebRequest = (ServletWebRequest) webRequest;
            HttpServletRequest request = servletWebRequest.getRequest();
            log.warn("{}:{} : {}", request.getMethod(), request.getRequestURI(), toSafeExceptionMessage(ex));
            log.warn(toSafeExceptionMessage(ex), ex);
        }
    }

    private String toSafeExceptionMessage(Exception ex) {
        String message = ex.getLocalizedMessage() == null ? ex.getMessage() : ex.getLocalizedMessage();
        return message == null ? "UNKNOWN" : message;
    }
}
