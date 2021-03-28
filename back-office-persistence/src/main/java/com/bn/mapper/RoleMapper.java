package com.bn.mapper;

import com.bn.persistence.RoleDO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleMapper {
    List<RoleDO> list();

    RoleDO selectByName(String name);

    RoleDO selectById(String id);

    void insert(RoleDO role);
}
