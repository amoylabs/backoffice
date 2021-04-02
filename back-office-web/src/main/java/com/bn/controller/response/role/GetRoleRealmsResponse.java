package com.bn.controller.response.role;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GetRoleRealmsResponse {
    private String roleId;
    private String roleName;
    private List<RealmVO> realms;

    @Data
    @Builder
    public static class RealmVO {
        private String id;
        private String name;
    }
}
