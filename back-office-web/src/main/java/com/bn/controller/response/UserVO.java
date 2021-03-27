package com.bn.controller.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserVO {
    private Long id;
    private String name;
    private String mobilePhone;
    private String email;
    private String status;
}
