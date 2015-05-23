package org.bahmni.module.admin.config.service;

import org.bahmni.module.admin.config.model.BahmniConfig;

public interface BahmniConfigService {
    BahmniConfig get(String appName, String configName);
}
