package com.bn.mapper;

import com.bn.persistence.UserDO;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper {
    UserDO selectByPrimaryKey(Long id);

    void insert(UserDO userDO);
}
