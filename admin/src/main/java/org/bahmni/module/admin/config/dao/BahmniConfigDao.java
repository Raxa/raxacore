package org.bahmni.module.admin.config.dao;

import org.bahmni.module.admin.config.model.BahmniConfig;

public interface BahmniConfigDao {
    BahmniConfig get(String appName, String configName);
}
