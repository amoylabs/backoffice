package com.bn.persistence;

import com.bn.domain.User;

public interface UserBuilder {
    static UserDO fromDomain(User user) {
        return UserDO.builder()
            .name(user.getName())
            .mobilePhone(user.getMobilePhone())
            .email(user.getEmail())
            .password(user.getPassword())
            .passwordSalt(user.getPasswordSalt())
            .status(user.getStatus())
            .build();
    }

    static User fromDO(UserDO userDO) {
        return User.builder()
            .name(userDO.getName())
            .mobilePhone(userDO.getMobilePhone())
            .email(userDO.getEmail())
            .password(userDO.getPassword())
            .passwordSalt(userDO.getPasswordSalt())
            .status(userDO.getStatus())
            .build();
    }
}
