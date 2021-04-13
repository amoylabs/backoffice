package com.bn.web;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private final String errorCode;
    private final List<String> errorMessages;
}
