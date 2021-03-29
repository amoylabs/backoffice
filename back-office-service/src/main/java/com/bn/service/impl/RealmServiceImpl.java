package com.bn.service.impl;

import com.bn.domain.Realm;
import com.bn.repository.RealmRepository;
import com.bn.service.RealmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RealmServiceImpl implements RealmService {
    private RealmRepository realmRepository;

    @Override
    public List<Realm> listRealmsByIds(List<Long> ids) {
        return realmRepository.findByIdIn(ids);
    }

    @Autowired
    public void setRealmRepository(RealmRepository realmRepository) {
        this.realmRepository = realmRepository;
    }
}
