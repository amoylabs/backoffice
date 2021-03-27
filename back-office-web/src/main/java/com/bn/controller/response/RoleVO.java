package com.bn.controller.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoleVO {
    private String id;
    private String name;
    private String description;
}
