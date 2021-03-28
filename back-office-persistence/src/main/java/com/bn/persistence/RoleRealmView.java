package com.bn.persistence;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class RoleRealmView {
    private String id;
    private String roleId;
    private String roleName;
    private String realmId;
    private String realmName;
}
