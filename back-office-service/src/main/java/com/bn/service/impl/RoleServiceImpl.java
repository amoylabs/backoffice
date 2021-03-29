package com.bn.service.impl;

import com.bn.domain.Realm;
import com.bn.domain.Role;
import com.bn.repository.RealmRepository;
import com.bn.repository.RoleRepository;
import com.bn.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class RoleServiceImpl implements RoleService {
    private RoleRepository roleRepository;
    private RealmRepository realmRepository;

    @Override
    public Role loadRole(Long id) {
        return roleRepository.findById(id).get();
    }

    @Override
    public List<Role> listRoles() {
        return roleRepository.findAll();
    }

    @Override
    public String createRole(Role role, String createdBy) {
        Objects.requireNonNull(role, "Role should not be null to create");
        role.initialize();
        return roleRepository.save(role).getName();
    }

    @Override
    public List<Realm> listRealms() {
        return realmRepository.findAll();
    }

    @Override
    public String createRealm(Realm realm, String createdBy) {
        Objects.requireNonNull(realm, "Realm should not be null to create");
        realm.initialize();
        return realmRepository.save(realm).getName();
    }

    @Override
    public void updateRoles(List<Role> roles, String userName) {
        // TODO
    }

    @Autowired
    public void setRoleRepository(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Autowired
    public void setRealmRepository(RealmRepository realmRepository) {
        this.realmRepository = realmRepository;
    }
}
