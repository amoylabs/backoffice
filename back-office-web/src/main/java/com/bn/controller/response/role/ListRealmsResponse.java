package com.bn.controller.response.role;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class ListRealmsResponse {
    List<RealmVO> roles;

    @Value
    @Builder
    public static class RealmVO {
        String id;
        String name;
        String description;
    }
}
