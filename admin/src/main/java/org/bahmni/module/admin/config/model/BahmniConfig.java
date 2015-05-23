package org.bahmni.module.admin.config.model;

import org.openmrs.BaseOpenmrsData;

public class BahmniConfig extends BaseOpenmrsData {
    private Integer configId;

    private String appName;

    private String configName;

    private String config;

    public Integer getConfigId() {
        return configId;
    }

    public void setConfigId(Integer configId) {
        this.configId = configId;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    @Override
    public Integer getId() {
        return getConfigId();
    }

    @Override
    public void setId(Integer id) {
        setConfigId(id);
    }
}
