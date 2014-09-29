package org.openmrs.module.bahmnicore.web.v1_0.controller;

import org.bahmni.module.bahmnicore.contract.observation.DiseaseTemplate;
import org.bahmni.module.bahmnicore.web.v1_0.controller.BaseWebControllerTest;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class DiseaseTemplateControllerIT extends BaseWebControllerTest {

    @Autowired
    DiseaseTemplateController diseaseTemplateController ;

    @Before
    public void setUp() throws Exception {
        executeDataSet("personObsTestData.xml");
    }

    @Test
    public void shouldReturnObsForAllDiseaseTemplatesWithIntakeAndProgressFromTheLatestVisit() throws Exception {
        List<LinkedHashMap> diseaseTemplates = deserialize(handle(newGetRequest("/rest/v1/bahmnicore/diseaseTemplates", new Parameter("patientUuid", "86526ed5-3c11-11de-a0ba-001e378eb67a"))), List.class);
        assertNotNull(diseaseTemplates);
        assertThat(diseaseTemplates.size(), is(1));
        assertThat((String)diseaseTemplates.get(0).get("name"),equalTo("Breast Cancer"));
        List<ArrayList> observations = (List<ArrayList>) diseaseTemplates.get(0).get("observations");
        ArrayList<LinkedHashMap> observationData = observations.get(0);
        assertThat(observationData.get(0).get("visitStartDate"), Matchers.<Object>is(1218997800000L));

        System.out.println(diseaseTemplates);
    }
}