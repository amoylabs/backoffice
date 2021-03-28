package com.bn.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetUserResponse {
    private Long id;
    private String name;
    @JsonProperty("mobile_phone")
    private String mobilePhone;
    private String email;
    private String status;
}
