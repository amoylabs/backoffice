package com.bn.repository.impl;

import com.bn.domain.App;
import com.bn.exception.NotFoundException;
import com.bn.mapper.AppMapper;
import com.bn.persistence.AppDO;
import com.bn.repository.AppRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AppRepositoryImpl implements AppRepository {
    private AppMapper appMapper;

    @Override
    public App get(String id) {
        AppDO appDO = appMapper.selectById(id);
        if (appDO == null) {
            throw new NotFoundException("app not found - " + id);
        }

        return App.builder().id(appDO.getId()).name(appDO.getName()).signingKey(appDO.getSigningKey()).build();
    }

    @Autowired
    public void setAppMapper(AppMapper appMapper) {
        this.appMapper = appMapper;
    }
}
