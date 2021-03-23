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

    private void generatePasswordSalt() {
        this.passwordSalt = PasswordUtils.getSalt();
    }

    public void initialize() {
        generatePasswordSalt();
        setStatus(UserStatus.ACTIVE);
    }
}
