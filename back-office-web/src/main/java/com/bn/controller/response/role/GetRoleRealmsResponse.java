package com.bn.controller.response.role;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GetRoleRealmsResponse {
    @JsonProperty("role_id")
    private String roleId;
    @JsonProperty("role_name")
    private String roleName;
    private List<RealmVO> realms;

    @Data
    @Builder
    public static class RealmVO {
        private String id;
        private String name;
    }
}
