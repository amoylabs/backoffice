package com.bn.controller.request;

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
public class CreateRealmRequest {
    @JsonProperty("name")
    @NotBlank(message = "realm name is required")
    private String name;

    @JsonProperty("description")
    @NotBlank(message = "realm description is required")
    private String description;
}
