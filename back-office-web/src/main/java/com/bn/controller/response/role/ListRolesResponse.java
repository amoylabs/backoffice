package com.bn.controller.response.role;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ListRolesResponse {
    private List<RoleVO> roles;

    @Data
    @Builder
    public static class RoleVO {
        private String id;
        private String name;
        private String description;
    }
}
