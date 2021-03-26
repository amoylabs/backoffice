package com.bn.authorization;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@EqualsAndHashCode(callSuper = false)
public class UserRealm {
    public static final String ADMINISTER_AUTH = "super";

    private String userId;
    private String userName;
    private List<String> realms; // user realms

    public static UserRealm anonymous() {
        return UserRealm.builder()
            .userId("0")
            .userName("anonymous")
            .realms(List.of())
            .build();
    }
}
