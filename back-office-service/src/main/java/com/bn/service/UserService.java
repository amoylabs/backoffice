package com.bn.service;

import com.bn.domain.User;

import java.util.Optional;

public interface UserService {
    User create(User user);

    Optional<User> get(Long id);
}
