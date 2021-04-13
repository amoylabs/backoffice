package com.bn.domain;

import com.bn.util.PasswordUtils;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
    private Long id;
    private String name;
    private String mobilePhone;
    private String email;
    private String password;
    private String passwordSalt;
    private UserStatus status;
    private String roleId;

    public void initialize() {
        final String passwordSalt = PasswordUtils.getSalt();
        setPassword(PasswordUtils.generateSecurePassword(password, passwordSalt));
        setPasswordSalt(passwordSalt);
        setStatus(UserStatus.ACTIVE);
    }
}
