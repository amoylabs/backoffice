package com.bn.controller.response.role;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class ListRolesResponse {
    List<RoleVO> roles;

    @Value
    @Builder
    public static class RoleVO {
        String id;
        String name;
        String description;
    }
}
