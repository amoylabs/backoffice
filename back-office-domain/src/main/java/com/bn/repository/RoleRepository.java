package com.bn.repository;

import com.bn.domain.Realm;
import com.bn.domain.Role;
import com.bn.domain.RoleRealmSetting;

import java.util.List;

public interface RoleRepository {
    List<Role> listRoles();

    String createRole(Role role, String createdBy);

    List<Realm> listRealms();

    String createRealm(Realm realm, String createdBy);

    void createRoleRealmsSetting(List<RoleRealmSetting> settings, String createdBy);
}
