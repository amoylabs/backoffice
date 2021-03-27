package com.bn.repository;

import com.bn.domain.Realm;
import com.bn.domain.Role;

import java.util.List;

public interface RoleRepository {
    List<Role> listRoles();

    String createRole(Role role, String createdBy);

    List<Realm> listRealms();

    String createRealm(Realm realm, String createdBy);
}
