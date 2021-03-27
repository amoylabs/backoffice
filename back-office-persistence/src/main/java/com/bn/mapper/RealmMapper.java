package com.bn.mapper;

import com.bn.persistence.RealmDO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RealmMapper {
    List<RealmDO> list();

    RealmDO selectByName(String name);

    void insert(RealmDO realm);
}
