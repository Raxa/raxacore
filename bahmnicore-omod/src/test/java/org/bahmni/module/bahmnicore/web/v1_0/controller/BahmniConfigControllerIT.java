package org.bahmni.module.bahmnicore.web.v1_0.controller;

import org.bahmni.module.admin.config.model.BahmniConfig;
import org.bahmni.test.web.controller.BaseWebControllerTest;
import org.codehaus.jackson.type.TypeReference;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class BahmniConfigControllerIT extends BaseWebControllerTest {
    @Before
    public void setUp() throws Exception {
        executeDataSet("configDataSetup.xml");
    }

    @Test
    public void deserialization_to_json_of_config() throws Exception {
        HashMap<String, String> headers = new HashMap<>();
//        headers.put("Accept", "application/json");
        BahmniConfig bahmniConfig = deserialize(handle(newGetRequest("/rest/v1/bahmni/config/get", headers, new Parameter("appName", "clinical"), new Parameter("configName", "app.json"))), new TypeReference<BahmniConfig>() {
        });
        System.out.println(bahmniConfig);
    }
}