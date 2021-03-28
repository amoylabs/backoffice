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

    RoleRealmSetting getRealms4Role(String roleId);

    void saveOrUpdateRoleRealmsSettings(List<RoleRealmSetting> settings, String createdBy);
}
