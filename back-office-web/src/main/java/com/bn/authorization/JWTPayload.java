package com.bn.authorization;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Builder
@Data
@EqualsAndHashCode(callSuper = false)
public class JWTPayload {
    private String sub; // subject
    private Long iat; // sign time
    private Long exp; // expire time
    private String jti; // JWT ID
    private String userId; // user id
    private List<String> authorities; // user authorities
}
