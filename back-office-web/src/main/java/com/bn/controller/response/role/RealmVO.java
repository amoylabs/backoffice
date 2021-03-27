package com.bn.controller.response.role;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RealmVO {
    private String id;
    private String name;
    private String description;
}
