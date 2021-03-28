package com.bn.mapper;

import com.bn.persistence.RoleRealmDO;
import com.bn.persistence.RoleRealmView;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRealmMapper {
    void insert(RoleRealmDO roleRealm);

    List<RoleRealmView> selectByRoleId(String roleId);

    void deleteByRoleId(String roleId);
}
