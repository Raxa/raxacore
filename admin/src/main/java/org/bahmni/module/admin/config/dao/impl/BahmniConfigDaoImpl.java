package org.bahmni.module.admin.config.dao.impl;

import org.apache.commons.collections.CollectionUtils;
import org.bahmni.module.admin.config.dao.BahmniConfigDao;
import org.bahmni.module.admin.config.model.BahmniConfig;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BahmniConfigDaoImpl implements BahmniConfigDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public BahmniConfig get(String appName, String configName) {
        List<BahmniConfig> appConfig = new ArrayList<>();
        Session currentSession = sessionFactory.getCurrentSession();
        Query query = currentSession.createQuery(
                "select config from BahmniConfig config " +
                        " where config.appName = :appName and config.configName = :configName");
        query.setParameter("appName", appName);
        query.setParameter("configName", configName);
        appConfig.addAll(query.list());
        return CollectionUtils.isEmpty(appConfig) ? null : appConfig.get(0);
    }

    @Override
    public BahmniConfig get(String uuid) {
        List<BahmniConfig> appConfig = new ArrayList<>();
        Session currentSession = sessionFactory.getCurrentSession();
        Query query = currentSession.createQuery(
                "select config from BahmniConfig config " +
                        " where config.uuid = :uuid ");
        query.setParameter("uuid", uuid);
        appConfig.addAll(query.list());
        return CollectionUtils.isEmpty(appConfig) ? null : appConfig.get(0);
    }

    //Mihir: Don't try to the merge the top one and this method together, since we are using a CLOB in MYSQL
    //its a streaming Datatype, so best not to load things we don't require in the memory.
    @Override
    public List<BahmniConfig> getAllFor(String appName) {
        List<BahmniConfig> appConfigs = new ArrayList<>();
        Session currentSession = sessionFactory.getCurrentSession();
        Query query = currentSession.createQuery(
                "select config from BahmniConfig config " +
                        " where config.appName = :appName ");
        query.setParameter("appName", appName);
        appConfigs.addAll(query.list());
        for (BahmniConfig bahmniConfig : appConfigs) {
            bahmniConfig.setConfig(null);
        }
        return appConfigs;
    }

    @Override
    public BahmniConfig save(BahmniConfig bahmniConfig) {
        sessionFactory.getCurrentSession().save(bahmniConfig);
        sessionFactory.getCurrentSession().flush();
        return get(bahmniConfig.getAppName(), bahmniConfig.getConfigName());
    }
}
