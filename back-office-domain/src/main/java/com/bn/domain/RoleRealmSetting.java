package com.bn.domain;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RoleRealmSetting {
    private Role role;
    private List<Realm> realms;
}
