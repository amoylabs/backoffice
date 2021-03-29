package com.bn.controller.request.role;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRoleRequest {
    @JsonProperty("name")
    @NotBlank(message = "role name is required")
    private String name;

    @JsonProperty("description")
    @NotBlank(message = "role description is required")
    private String description;
}