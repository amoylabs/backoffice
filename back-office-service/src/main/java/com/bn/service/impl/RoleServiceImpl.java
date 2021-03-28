package com.bn.service.impl;

import com.bn.domain.Realm;
import com.bn.domain.Role;
import com.bn.domain.RoleRealmSetting;
import com.bn.repository.RoleRepository;
import com.bn.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class RoleServiceImpl implements RoleService {
    private RoleRepository roleRepository;

    @Override
    public List<Role> listRoles() {
        return roleRepository.listRoles();
    }

    @Override
    public String createRole(Role role, String createdBy) {
        Objects.requireNonNull(role, "Role should not be null to create");
        role.initialize();
        return roleRepository.createRole(role, createdBy);
    }

    @Override
    public List<Realm> listRealms() {
        return roleRepository.listRealms();
    }

    @Override
    public String createRealm(Realm realm, String createdBy) {
        Objects.requireNonNull(realm, "Realm should not be null to create");
        realm.initialize();
        return roleRepository.createRealm(realm, createdBy);
    }

    @Override
    public RoleRealmSetting getRealms4Role(String roleId) {
        return roleRepository.getRealms4Role(roleId);
    }

    @Override
    public void saveOrUpdateRoleRealmsSettings(List<RoleRealmSetting> settings, String createdBy) {
        roleRepository.saveOrUpdateRoleRealmsSettings(settings, createdBy);
    }

    @Autowired
    public void setRoleRepository(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }
}
