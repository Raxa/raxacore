package org.bahmni.module.admin.config.dao.impl;

import org.bahmni.module.admin.config.model.BahmniConfig;
import org.databene.commons.StringUtil;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class BahmniConfigDaoImplIT extends BaseModuleWebContextSensitiveTest {
    @Autowired
    private BahmniConfigDaoImpl configDao;

    @Before
    public void setUp() throws Exception {
        executeDataSet("configDataSetup.xml");
    }

    @Test
    public void get_config_from_by_app_and_config_name() throws Exception {
        BahmniConfig clinical = configDao.get("clinical", "app.json");
        assertNotNull(clinical);
        assertEquals("clinical", clinical.getAppName());
        assertEquals("app.json", clinical.getConfigName());
        assertEquals("0aa1efd4-6eeb-4cea-bd4b-94af86f24d97", clinical.getUuid());
        assertFalse(StringUtil.isEmpty(clinical.getConfig()));
    }


    @Test
    public void return_null_if_config_not_available() throws Exception {
        BahmniConfig clinical = configDao.get("notclinical", "app.json");
        assertNull(clinical);
    }
}