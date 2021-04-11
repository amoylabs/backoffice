package com.bn.service.impl;

import com.bn.domain.User;
import com.bn.repository.UserRepository;
import com.bn.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;

    @Override
    public Long create(User user, String createdBy) {
        user.initialize();
        return userRepository.save(user, createdBy);
    }

    @Override
    public User get(Long id) {
        return userRepository.get(id);
    }

    @Override
    public User get(String userName) {
        return userRepository.get(userName);
    }

    @Async
    @Override
    public void doSthAsync() {
        try {
            Thread.sleep(1_000 * 5);
            log.info("Take a 5s snap ...");
        } catch (InterruptedException ex) {
            log.error("Async method failed to execute", ex);
        }
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
