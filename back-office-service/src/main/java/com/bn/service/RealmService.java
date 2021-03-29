package com.bn.service;

import com.bn.domain.Realm;

import java.util.List;

public interface RealmService {
    List<Realm> listRealmsByIds(List<Long> realmIds);
}
