package org.bahmni.module.admin.config.service.impl;

import org.bahmni.module.admin.config.dao.BahmniConfigDao;
import org.bahmni.module.admin.config.model.BahmniConfig;
import org.bahmni.module.admin.config.service.BahmniConfigService;
import org.openmrs.api.context.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

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

    @Override
    public List<BahmniConfig> getAllFor(String appName) {
        return bahmniConfigDao.getAllFor(appName);
    }

    @Override
    public BahmniConfig save(BahmniConfig bahmniConfig) {
        bahmniConfig.setCreator(Context.getAuthenticatedUser());
        bahmniConfig.setDateCreated(new Date());
        return bahmniConfigDao.save(bahmniConfig);
    }
}
