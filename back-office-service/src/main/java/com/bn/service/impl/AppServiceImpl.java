package com.bn.service.impl;

import com.bn.domain.App;
import com.bn.repository.AppRepository;
import com.bn.service.AppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppServiceImpl implements AppService {
    private AppRepository appRepository;

    @Override
    public App get(String id) {
        return appRepository.get(id);
    }

    @Autowired
    public void setAppRepository(AppRepository appRepository) {
        this.appRepository = appRepository;
    }
}
