package com.bn.service;

import com.bn.domain.User;

public interface UserService {
    Long create(User user, String createdBy);

    User get(Long id);
}
