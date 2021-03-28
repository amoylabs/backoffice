package com.bn.domain;

import com.bn.util.PasswordUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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
