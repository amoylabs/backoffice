package com.bn.repository.impl;

import com.bn.domain.Realm;
import com.bn.domain.Role;
import com.bn.domain.RoleRealmSetting;
import com.bn.exception.ConflictException;
import com.bn.mapper.RealmMapper;
import com.bn.mapper.RoleMapper;
import com.bn.mapper.RoleRealmMapper;
import com.bn.persistence.RealmDO;
import com.bn.persistence.RoleDO;
import com.bn.persistence.RoleRealmDO;
import com.bn.repository.RoleRepository;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class RoleRepositoryImpl implements RoleRepository {
    private RoleMapper roleMapper;
    private RealmMapper realmMapper;
    // private RoleRealmMapper roleRealmMapper;
    private SqlSessionFactory sqlSessionFactory;

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

    @Override
    @Transactional
    public void createRoleRealmsSetting(List<RoleRealmSetting> settings, String createdBy) {
        // if (settings == null || settings.isEmpty()) {
        //     throw new BadRequest()
        // }
        List<RoleRealmDO> roleRealmDOList = settings.stream()
            .map(RoleRealmSetting::transform2OneByOne)
            .reduce(new ArrayList<>(), (memo, subList) -> {
                memo.addAll(subList);
                return memo;
            })
            .stream()
            .map(pair -> RoleRealmDO.builder()
                .id(UUID.randomUUID().toString())
                .roleId(pair.getValue0().getId())
                .realmId(pair.getValue1().getId())
                .createdTime(ZonedDateTime.now())
                .createdBy(createdBy)
                .build())
            .collect(Collectors.toList());
        try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
            RoleRealmMapper mapper = sqlSession.getMapper(RoleRealmMapper.class);
            int size = roleRealmDOList.size();
            int idx = 0;
            while (idx < size) {
                mapper.insert(roleRealmDOList.get(idx));
                idx++;
                if (idx % 10 == 0 || idx == size) {
                    sqlSession.flushStatements();
                    sqlSession.clearCache();
                }
            }
            sqlSession.commit();
        }
    }

    @Autowired
    public void setRoleMapper(RoleMapper roleMapper) {
        this.roleMapper = roleMapper;
    }

    @Autowired
    public void setRealmMapper(RealmMapper realmMapper) {
        this.realmMapper = realmMapper;
    }

    // @Autowired
    // public void setRoleRealmMapper(RoleRealmMapper roleRealmMapper) {
    //     this.roleRealmMapper = roleRealmMapper;
    // }

    @Autowired
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }
}
