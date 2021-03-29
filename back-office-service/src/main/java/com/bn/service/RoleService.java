package com.bn.service;

import com.bn.domain.Realm;
import com.bn.domain.Role;

import java.util.List;

public interface RoleService {
    Role loadRole(Long id);

    List<Role> listRoles();

    String createRole(Role role, String createdBy);

    List<Realm> listRealms();

    String createRealm(Realm realm, String createdBy);

    void updateRoles(List<Role> roles, String userName);
}
