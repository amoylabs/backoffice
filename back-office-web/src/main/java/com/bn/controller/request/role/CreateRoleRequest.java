package com.bn.controller.request.role;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
public class CreateRoleRequest {
    @NotBlank(message = "role name is required")
    private String name;

    @NotBlank(message = "role description is required")
    private String description;
}
