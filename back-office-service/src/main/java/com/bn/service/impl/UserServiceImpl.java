package com.bn.service.impl;

import com.bn.domain.User;
import com.bn.repository.UserRepository;
import com.bn.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;

    @Override
    public Long create(User user) {
        user.initialize();
        return userRepository.save(user);
    }

    @Override
    public User get(Long id) {
        return userRepository.get(id);
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
