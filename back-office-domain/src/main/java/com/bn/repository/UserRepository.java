package com.bn.repository;

import com.bn.domain.User;

public interface UserRepository {
    User get(Long id);

    User get(String userName);

    Long save(User user, String createdBy);
}
