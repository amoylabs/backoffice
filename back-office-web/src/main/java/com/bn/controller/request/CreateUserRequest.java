package com.bn.controller.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {
    @JsonProperty("name")
    @NotBlank(message = "user name is required")
    private String name;

    @JsonProperty("mobile_phone")
    private String mobilePhone;

    @JsonProperty("email")
    @Email
    private String email;

    @NotBlank(message = "password is required")
    @JsonProperty("password")
    private String password;
}
