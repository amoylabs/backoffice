package com.bn.authorization;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Builder
@Data
@EqualsAndHashCode(callSuper = false)
public class UserAuthorization {
    public static final String USER_AUTH_CONTEXT_NAME = "__user_auth";
    public static final String ADMINISTER_AUTH = "super";

    private String userId;
    private String userName;
    private List<String> authorities; // user authorities
}
