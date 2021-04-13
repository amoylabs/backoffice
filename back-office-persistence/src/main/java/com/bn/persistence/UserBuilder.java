package com.bn.persistence;

import com.bn.domain.User;

public interface UserBuilder {
    static UserDO fromDomain(User user) {
        return UserDO.builder()
            .id(user.getId())
            .name(user.getName())
            .mobilePhone(user.getMobilePhone())
            .email(user.getEmail())
            .password(user.getPassword())
            .passwordSalt(user.getPasswordSalt())
            .status(user.getStatus())
            .roleId(user.getRoleId())
            .build();
    }

    static User fromDO(UserDO userDO) {
        return User.builder()
            .id(userDO.getId())
            .name(userDO.getName())
            .mobilePhone(userDO.getMobilePhone())
            .email(userDO.getEmail())
            .password(userDO.getPassword())
            .passwordSalt(userDO.getPasswordSalt())
            .status(userDO.getStatus())
            .roleId(userDO.getRoleId())
            .build();
    }
}
