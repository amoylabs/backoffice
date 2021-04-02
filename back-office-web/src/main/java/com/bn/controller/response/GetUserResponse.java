package com.bn.controller.response;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class GetUserResponse {
    Long id;
    String name;
    String mobilePhone;
    String email;
    String status;
}
