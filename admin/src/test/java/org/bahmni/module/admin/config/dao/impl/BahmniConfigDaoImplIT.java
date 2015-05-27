package org.bahmni.module.admin.config.dao.impl;

import org.bahmni.module.admin.config.dao.BahmniConfigDao;
import org.bahmni.module.admin.config.model.BahmniConfig;
import org.databene.commons.StringUtil;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class BahmniConfigDaoImplIT extends BaseModuleWebContextSensitiveTest {
    @Autowired
    private BahmniConfigDao bahmniConfigDao;

    @Before
    public void setUp() throws Exception {
        executeDataSet("configDataSetup.xml");
    }

    @Test
    public void get_config_from_by_app_and_config_name() throws Exception {
        BahmniConfig clinical = bahmniConfigDao.get("clinical", "app.json");
        assertNotNull(clinical);
        assertEquals("clinical", clinical.getAppName());
        assertEquals("app.json", clinical.getConfigName());
        assertEquals("0aa1efd4-6eeb-4cea-bd4b-94af86f24d97", clinical.getUuid());
        assertFalse(StringUtil.isEmpty(clinical.getConfig()));
    }

    @Test
    public void get_config_from_by_uuid() throws Exception {
        BahmniConfig clinical = bahmniConfigDao.get("0aa1efd4-6eeb-4cea-bd4b-94af86f24d97");
        assertNotNull(clinical);
        assertEquals("clinical", clinical.getAppName());
        assertEquals("app.json", clinical.getConfigName());
        assertEquals("0aa1efd4-6eeb-4cea-bd4b-94af86f24d97", clinical.getUuid());
        assertFalse(StringUtil.isEmpty(clinical.getConfig()));
    }

    @Test
    public void return_null_if_config_not_available() throws Exception {
        BahmniConfig clinical = bahmniConfigDao.get("notclinical", "app.json");
        assertNull(clinical);
    }

    @Test
    public void get_all_configs_for() throws Exception {
        List<BahmniConfig> clinical = bahmniConfigDao.getAllFor("clinical");
        assertEquals(2, clinical.size());
    }

    @Test
    public void insert_new_config() throws Exception {
        BahmniConfig bahmniConfig = new BahmniConfig();
        bahmniConfig.setConfig("New Config");
        bahmniConfig.setAppName("registration");
        bahmniConfig.setConfigName("app.json");
        bahmniConfig.setCreator(Context.getUserContext().getAuthenticatedUser());
        bahmniConfig.setDateCreated(new Date());
        BahmniConfig add = bahmniConfigDao.save(bahmniConfig);
        assertEquals("registration", add.getAppName());
        assertEquals("app.json", add.getConfigName());
        assertEquals("New Config", add.getConfig());
    }

    @Test
    public void update_config() throws Exception {
        BahmniConfig clinical = bahmniConfigDao.get("clinical", "app.json");
        clinical.setConfig("Modified Config");
        BahmniConfig add = bahmniConfigDao.update(clinical);
        BahmniConfig modifiedClinical = bahmniConfigDao.get("clinical", "app.json");
        assertEquals("clinical", modifiedClinical.getAppName());
        assertEquals("app.json", modifiedClinical.getConfigName());
        assertEquals("Modified Config", modifiedClinical.getConfig());
        assertEquals(add, modifiedClinical);
    }
}