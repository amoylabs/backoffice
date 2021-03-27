package com.bn.mapper;

import com.bn.persistence.RealmDO;
import com.bn.persistence.RoleRealmDO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRealmMapper {
    void insert(RoleRealmDO roleRealm);

    List<RoleRealmDO> list();

    RealmDO selectByRoleId(String roleId);
}
