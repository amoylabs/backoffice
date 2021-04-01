package com.bn.controller.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Setter
@Getter
@Builder
public class CreateUserRequest {
    @NotBlank(message = "user name is required")
    private String name;

    private String mobilePhone;

    @Email
    private String email;

    @NotBlank(message = "password is required")
    private String password;
}
