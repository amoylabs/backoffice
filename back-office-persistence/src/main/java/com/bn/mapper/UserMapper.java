package com.bn.mapper;

import com.bn.persistence.UserDO;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper {
    UserDO selectByPrimaryKey(Long id);

    UserDO selectByName(String name);

    void insert(UserDO user);
}
