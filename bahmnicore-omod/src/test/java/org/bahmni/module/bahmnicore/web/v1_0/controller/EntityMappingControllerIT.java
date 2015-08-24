package org.bahmni.module.bahmnicore.web.v1_0.controller;

import org.bahmni.module.bahmnicore.contract.entityMapping.Entity;
import org.bahmni.module.bahmnicore.web.v1_0.BaseIntegrationTest;
import org.bahmni.module.bahmnicore.web.v1_0.mapper.CustomObjectMapper;
import org.bahmni.test.web.controller.BaseWebControllerTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.Assert.assertEquals;

public class EntityMappingControllerIT extends BaseIntegrationTest {

    private String ENTITY_MAPPINGS_URL = "/rest/v1/bahmnicore/entityMappings";

    @Before
    public void setUp() throws Exception {
        executeDataSet("entityMappingData.xml");
    }

    @Test
    public void shouldGetEntityMappings() throws Exception {

        MockHttpServletRequest mockHttpServletRequest = newGetRequest(ENTITY_MAPPINGS_URL,
                new BaseWebControllerTest.Parameter("entity1Uuid", "5dc2a3b0-863c-4074-8f84-45762c3aa04c"),
                new BaseWebControllerTest.Parameter("entityMappingType", "program_obstemplates"));

        MockHttpServletResponse response = handle(mockHttpServletRequest);
//        Entity entityResponse = new CustomObjectMapper().readValue(response.getContentAsString(), Entity.class);
        System.out.println(response.getContentAsString());

//        assertEquals(2, entityResponse.getMappings().size());
    }
}
