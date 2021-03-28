package com.bn.repository.impl;

import com.bn.domain.Realm;
import com.bn.domain.Role;
import com.bn.domain.RoleRealmSetting;
import com.bn.exception.ConflictException;
import com.bn.exception.ResourceNotFoundException;
import com.bn.mapper.RealmMapper;
import com.bn.mapper.RoleMapper;
import com.bn.mapper.RoleRealmMapper;
import com.bn.persistence.RealmDO;
import com.bn.persistence.RoleDO;
import com.bn.persistence.RoleRealmDO;
import com.bn.persistence.RoleRealmView;
import com.bn.repository.RoleRepository;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class RoleRepositoryImpl implements RoleRepository {
    private RoleMapper roleMapper;
    private RealmMapper realmMapper;
    private RoleRealmMapper roleRealmMapper;
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
            throw new ConflictException("conflicting role - " + role.getName());
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
            throw new ConflictException("conflicting realm - " + realm.getName());
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
    public RoleRealmSetting getRealms4Role(String roleId) {
        RoleDO existingRole = roleMapper.selectById(roleId);
        if (existingRole == null) {
            throw new ResourceNotFoundException("role not found - " + roleId);
        }

        List<RoleRealmView> results = roleRealmMapper.selectByRoleId(roleId);
        if (results == null || results.isEmpty()) {
            return RoleRealmSetting.builder().build();
        }

        // all records will belong to one role so that it's safe to get the role from the first record
        return RoleRealmSetting.builder()
            .role(Role.builder().id(results.get(0).getRoleId()).name(results.get(0).getRoleName()).build())
            .realms(results.stream().map(view -> Realm.builder().id(view.getRealmId()).name(view.getRealmName()).build()).collect(Collectors.toList())).build();
    }

    @Override
    @Transactional
    public void saveOrUpdateRoleRealmsSettings(List<RoleRealmSetting> settings, String createdBy) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
            RoleRealmMapper mapper = sqlSession.getMapper(RoleRealmMapper.class);
            for (RoleRealmSetting setting : settings) {
                List<RoleRealmDO> roleRealmDOList = convertToRoleRealmDO(setting, createdBy);
                mapper.deleteByRoleId(setting.getRole().getId()); // clear all existing data before re-create
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
            }
            sqlSession.commit();
        }
    }

    private List<RoleRealmDO> convertToRoleRealmDO(RoleRealmSetting setting, String createdBy) {
        return setting.getRealms().stream().map(realm -> RoleRealmDO.builder()
            .id(UUID.randomUUID().toString())
            .roleId(setting.getRole().getId())
            .realmId(realm.getId())
            .createdTime(ZonedDateTime.now())
            .createdBy(createdBy)
            .build()).collect(Collectors.toList());
    }

    @Autowired
    public void setRoleMapper(RoleMapper roleMapper) {
        this.roleMapper = roleMapper;
    }

    @Autowired
    public void setRealmMapper(RealmMapper realmMapper) {
        this.realmMapper = realmMapper;
    }

    @Autowired
    public void setRoleRealmMapper(RoleRealmMapper roleRealmMapper) {
        this.roleRealmMapper = roleRealmMapper;
    }

    @Autowired
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }
}
