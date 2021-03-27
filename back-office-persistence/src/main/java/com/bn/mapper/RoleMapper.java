package com.bn.mapper;

import com.bn.persistence.RoleDO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleMapper {
    List<RoleDO> list();

    RoleDO selectByName(String name);

    void insert(RoleDO role);
}
