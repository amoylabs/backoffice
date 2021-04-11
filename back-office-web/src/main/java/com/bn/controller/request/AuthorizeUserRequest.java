package com.bn.controller.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
@Builder
public class AuthorizeUserRequest {
    @NotBlank(message = "user login failure")
    private String un;

    @NotBlank(message = "user login failure")
    private String pd;

    @NotBlank(message = "user login failure")
    private String pk;
}
