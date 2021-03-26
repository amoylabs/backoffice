package com.bn.repository.impl;

import com.bn.domain.User;
import com.bn.exception.ResourceNotFoundException;
import com.bn.mapper.UserMapper;
import com.bn.persistence.UserBuilder;
import com.bn.persistence.UserDO;
import com.bn.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;

@Repository
@Slf4j
public class UserRepositoryImpl implements UserRepository {
    private UserMapper userMapper;

    @Override
    public User get(Long id) {
        UserDO userDO = userMapper.selectByPrimaryKey(id);
        if (userDO == null) throw new ResourceNotFoundException("user not found - " + id);
        return UserBuilder.fromDO(userDO);
    }

    @Override
    public Long save(User user, String createdBy) {
        UserDO userDO = UserBuilder.fromDomain(user);
        userDO.setCreatedTime(ZonedDateTime.now());
        userDO.setCreatedBy(createdBy);
        userMapper.insert(userDO);
        log.info("User Repository save data and return id - {}", userDO.getId());
        return userDO.getId();
    }

    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }
}
