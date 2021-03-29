package com.bn.repository;

import com.bn.domain.Realm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RealmRepository extends JpaRepository<Realm, Long> {
    List<Realm> findByIdIn(List<Long> realmIds);
}
