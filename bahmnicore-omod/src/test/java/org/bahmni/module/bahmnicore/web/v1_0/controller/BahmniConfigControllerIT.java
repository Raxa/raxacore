package org.bahmni.module.bahmnicore.web.v1_0.controller;

import org.bahmni.module.admin.config.model.BahmniConfig;
import org.bahmni.test.web.controller.BaseWebControllerTest;
import org.codehaus.jackson.type.TypeReference;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;

import static junit.framework.Assert.assertNull;
import static junit.framework.TestCase.assertEquals;

@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class BahmniConfigControllerIT extends BaseWebControllerTest {
    @Before
    public void setUp() throws Exception {
        executeDataSet("configDataSetup.xml");
    }

    @Test
    public void deserialization_to_json_of_config() throws Exception {
        HashMap<String, String> headers = new HashMap<>();
        BahmniConfig bahmniConfig = deserialize(handle(newGetRequest("/rest/v1/bahmni/config", headers, new Parameter("appName", "clinical"), new Parameter("configName", "app.json"))), new TypeReference<BahmniConfig>() {
        });
        assertEquals("app.json", bahmniConfig.getAppName());
        assertEquals("clinical", bahmniConfig.getConfigName());
    }

    @Test
    public void stripped_down_json_of_all_configs_under_an_app() throws Exception {
        HashMap<String, String> headers = new HashMap<>();
        List<BahmniConfig> bahmniConfigs = deserialize(handle(newGetRequest("/rest/v1/bahmni/config/all", headers, new Parameter("appName", "clinical"))), new TypeReference<List<BahmniConfig>>() {
        });
        assertEquals(2, bahmniConfigs.size());
        assertNull( bahmniConfigs.get(0).getConfig());
        assertNull( bahmniConfigs.get(1).getConfig());
    }
}