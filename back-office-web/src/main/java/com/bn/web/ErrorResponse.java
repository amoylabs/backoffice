package com.bn.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    @JsonProperty("error_code")
    private final String errorCode;
    @JsonProperty("error_messages")
    private final List<String> messages;
}
