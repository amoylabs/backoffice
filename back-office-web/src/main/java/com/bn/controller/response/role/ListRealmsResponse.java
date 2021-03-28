package com.bn.controller.response.role;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ListRealmsResponse {
    private List<RealmVO> roles;

    @Data
    @Builder
    public static class RealmVO {
        private String id;
        private String name;
        private String description;
    }
}
