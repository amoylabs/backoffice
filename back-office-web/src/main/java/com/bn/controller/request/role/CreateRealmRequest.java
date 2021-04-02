package com.bn.controller.request.role;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
public class CreateRealmRequest {
    @NotBlank(message = "realm name is required")
    private String name;

    @NotBlank(message = "realm description is required")
    private String description;
}
