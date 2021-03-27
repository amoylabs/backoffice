package com.bn.domain;

import lombok.Builder;
import lombok.Data;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class RoleRealmSetting {
    private Role role;
    private List<Realm> realms;

    public List<Pair<Role, Realm>> transform2OneByOne() {
        List<Pair<Role, Realm>> roleRealmPairList = new ArrayList<>(realms.size());
        for (Realm realm : realms) {
            roleRealmPairList.add(Pair.with(role, realm));
        }
        return roleRealmPairList;
    }
}
