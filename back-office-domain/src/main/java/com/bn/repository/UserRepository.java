package com.bn.repository;

import com.bn.domain.User;

public interface UserRepository {
    User get(Long id);

    Long save(User user);
}
