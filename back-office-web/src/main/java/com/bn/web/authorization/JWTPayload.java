package com.bn.web.authorization;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Builder
@Data
@EqualsAndHashCode(callSuper = false)
public class JWTPayload {
    private String sub; // subject
    private String iss; // issuer
    private String aud; // audience
    private Long iat; // sign time
    private Long exp; // expire time
    private String jti; // JWT ID
    private UserRealm realm; // user realm
}
