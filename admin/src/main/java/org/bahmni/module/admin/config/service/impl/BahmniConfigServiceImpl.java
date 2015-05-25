package org.bahmni.module.admin.config.service.impl;

import org.bahmni.module.admin.config.dao.BahmniConfigDao;
import org.bahmni.module.admin.config.model.BahmniConfig;
import org.bahmni.module.admin.config.service.BahmniConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BahmniConfigServiceImpl implements BahmniConfigService {
    private BahmniConfigDao bahmniConfigDao;

    @Autowired
    public BahmniConfigServiceImpl(BahmniConfigDao bahmniConfigDao) {
        this.bahmniConfigDao = bahmniConfigDao;
    }

    @Override
    public BahmniConfig get(String appName, String configName) {
        return bahmniConfigDao.get(appName, configName);
    }
}
