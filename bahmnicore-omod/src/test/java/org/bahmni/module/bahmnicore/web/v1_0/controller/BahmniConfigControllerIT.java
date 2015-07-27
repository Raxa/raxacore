package org.bahmni.module.bahmnicore.web.v1_0.controller;

import org.bahmni.module.admin.config.model.BahmniConfig;
import org.bahmni.module.bahmnicore.web.v1_0.BaseIntegrationTest;
import org.bahmni.test.web.controller.BaseWebControllerTest;
import org.codehaus.jackson.type.TypeReference;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;

import static junit.framework.TestCase.*;


public class BahmniConfigControllerIT extends BaseIntegrationTest {
    @Before
    public void setUp() throws Exception {
        executeDataSet("configDataSetup.xml");
    }

    @Test
    public void deserialization_to_json_of_config() throws Exception {
        HashMap<String, String> headers = new HashMap<>();
        BahmniConfig bahmniConfig = deserialize(handle(newGetRequest("/rest/v1/bahmni/config", headers, new Parameter("appName", "clinical"), new Parameter("configName", "app.json"))), new TypeReference<BahmniConfig>() {
        });
        assertEquals("app.json", bahmniConfig.getConfigName());
        assertEquals("clinical", bahmniConfig.getAppName());
    }

    @Test
    public void get_config_by_path() throws Exception {
        String bahmniConfig = handle(newGetRequest("/rest/v1/bahmni/config/clinical/app.json")).getContentAsString();
        assertFalse(bahmniConfig.isEmpty());
        assertTrue(bahmniConfig.contains("bahmni.registration"));
    }

    @Test
    public void stripped_down_json_of_all_configs_under_an_app() throws Exception {
        HashMap<String, String> headers = new HashMap<>();
        List<BahmniConfig> bahmniConfigs = deserialize(handle(newGetRequest("/rest/v1/bahmni/config/all", headers, new Parameter("appName", "clinical"))), new TypeReference<List<BahmniConfig>>() {
        });
        assertEquals(2, bahmniConfigs.size());
        assertNull(bahmniConfigs.get(0).getConfig());
        assertNull(bahmniConfigs.get(1).getConfig());
    }

    @Test
    public void create_new_config() throws Exception {
        BahmniConfig bahmniConfig = new BahmniConfig();
        bahmniConfig.setConfig("New Config");
        bahmniConfig.setAppName("registration");
        bahmniConfig.setConfigName("app.json");
        BahmniConfig savedConfig = deserialize(handle(newPostRequest("/rest/v1/bahmni/config", bahmniConfig)), BahmniConfig.class);
        BahmniConfig getConfig = deserialize(handle(newGetRequest("/rest/v1/bahmni/config", new Parameter("appName", "registration"), new Parameter("configName", "app.json"))), BahmniConfig.class);
        assertEquals(savedConfig, getConfig);
        assertNotNull(getConfig.getDateCreated());
        assertEquals("New Config", getConfig.getConfig());
    }

    @Test
    public void update_existing_config() throws Exception {
        BahmniConfig getConfig = deserialize(handle(newGetRequest("/rest/v1/bahmni/config", new Parameter("appName", "clinical"), new Parameter("configName", "app.json"))), BahmniConfig.class);
        getConfig.setConfig("Updated Config");
        BahmniConfig savedConfig = deserialize(handle(newPutRequest("/rest/v1/bahmni/config", getConfig)), BahmniConfig.class);
        getConfig = deserialize(handle(newGetRequest("/rest/v1/bahmni/config", new Parameter("appName", "clinical"), new Parameter("configName", "app.json"))), BahmniConfig.class);
        assertEquals(savedConfig, getConfig);
        assertNotNull(getConfig.getDateCreated());
        assertEquals("Updated Config", getConfig.getConfig());
    }
}