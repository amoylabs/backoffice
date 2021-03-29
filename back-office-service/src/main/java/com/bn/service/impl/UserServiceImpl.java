package com.bn.service.impl;

import com.bn.domain.User;
import com.bn.exception.ConflictException;
import com.bn.repository.UserRepository;
import com.bn.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;

    @Override
    public Long create(User user, String createdBy) {
        user.initialize();

        User existingUser = userRepository.findByName(user.getName());
        if (existingUser != null) {
            throw new ConflictException("conflicting user - " + user.getName());
        }

        User newUser = userRepository.save(user);
        return newUser.getId();
    }

    @Override
    public User get(Long id) {
        return userRepository.findById(id).get();
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
