package com.bn.web.authorization;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@Builder
@EqualsAndHashCode(callSuper = false)
public class UserRealm {
    public static final String SUPER = "__super";

    public static UserRealm anonymous() {
        return UserRealm.builder()
            .userId("0")
            .userName("anonymous")
            .realms(List.of())
            .build();
    }

    private String userId;
    private String userName;
    private List<String> realms; // user realms
}
