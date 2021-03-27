package com.bn.repository.impl;

import com.bn.domain.Realm;
import com.bn.domain.Role;
import com.bn.exception.ConflictException;
import com.bn.mapper.RealmMapper;
import com.bn.mapper.RoleMapper;
import com.bn.persistence.RealmDO;
import com.bn.persistence.RoleDO;
import com.bn.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class RoleRepositoryImpl implements RoleRepository {
    private RoleMapper roleMapper;
    private RealmMapper realmMapper;

    @Override
    public List<Role> listRoles() {
        return roleMapper.list().stream().map(data -> Role.builder()
            .id(data.getId())
            .name(data.getName())
            .description(data.getDescription())
            .build())
            .collect(Collectors.toList());
    }

    @Override
    public String createRole(Role role, String createdBy) {
        RoleDO existingRole = roleMapper.selectByName(role.getName());
        if (existingRole != null) {
            throw new ConflictException("role is existing - " + role.getName());
        }

        RoleDO roleDO = RoleDO.builder()
            .id(role.getId())
            .name(role.getName())
            .description(role.getDescription())
            .build();
        roleDO.setCreatedTime(ZonedDateTime.now());
        roleDO.setCreatedBy(createdBy);
        roleMapper.insert(roleDO);
        return roleDO.getId();
    }

    @Override
    public List<Realm> listRealms() {
        return realmMapper.list().stream().map(data -> Realm.builder()
            .id(data.getId())
            .name(data.getName())
            .description(data.getDescription())
            .build())
            .collect(Collectors.toList());
    }

    @Override
    public String createRealm(Realm realm, String createdBy) {
        RealmDO existingRealm = realmMapper.selectByName(realm.getName());
        if (existingRealm != null) {
            throw new ConflictException("realm is existing - " + realm.getName());
        }

        RealmDO realmDO = RealmDO.builder()
            .id(realm.getId())
            .name(realm.getName())
            .description(realm.getDescription())
            .build();
        realmDO.setCreatedTime(ZonedDateTime.now());
        realmDO.setCreatedBy(createdBy);
        realmMapper.insert(realmDO);
        return realmDO.getId();
    }

    @Autowired
    public void setRoleMapper(RoleMapper roleMapper) {
        this.roleMapper = roleMapper;
    }

    @Autowired
    public void setRealmMapper(RealmMapper realmMapper) {
        this.realmMapper = realmMapper;
    }
}
