package com.bn.repository.impl;

import com.bn.domain.User;
import com.bn.exception.ConflictException;
import com.bn.exception.NotFoundException;
import com.bn.mapper.UserMapper;
import com.bn.persistence.UserBuilder;
import com.bn.persistence.UserDO;
import com.bn.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private UserMapper userMapper;

    @Override
    public User get(Long id) {
        UserDO userDO = userMapper.selectByPrimaryKey(id);
        if (userDO == null) {
            throw new NotFoundException("user not found - " + id);
        }

        return UserBuilder.fromDO(userDO);
    }

    @Override
    public User get(String userName) {
        UserDO userDO = userMapper.selectByName(userName);
        if (userDO == null) {
            throw new NotFoundException("user not found - " + userName);
        }

        return UserBuilder.fromDO(userDO);
    }

    @Override
    public Long save(User user, String createdBy) {
        UserDO existingUser = userMapper.selectByName(user.getName());
        if (existingUser != null) {
            throw new ConflictException("conflicting user - " + user.getName());
        }

        UserDO userDO = UserBuilder.fromDomain(user);
        userDO.setCreatedTime(ZonedDateTime.now());
        userDO.setCreatedBy(createdBy);
        userMapper.insert(userDO);
        return userDO.getId();
    }

    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }
}
