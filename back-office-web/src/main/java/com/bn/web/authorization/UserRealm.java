package com.bn.web.authorization;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

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
            .isAnonymous(true)
            .realms(List.of())
            .build();
    }

    private String userId;
    private String userName;
    private List<String> realms; // user realms

    @JsonIgnore
    @Setter(AccessLevel.NONE)
    private boolean isAnonymous;
}
