package org.openmrs.module.bahmnicore.web.v1_0.controller;

import org.bahmni.module.bahmnicore.contract.diseasetemplate.DiseaseTemplate;
import org.bahmni.module.bahmnicore.contract.diseasetemplate.ObservationTemplate;
import org.bahmni.test.util.DateUtils;
import org.bahmni.test.web.controller.BaseWebControllerTest;
import org.codehaus.jackson.type.TypeReference;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.ObsService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class DiseaseTemplateControllerIT extends BaseWebControllerTest {

    @Autowired
    DiseaseTemplateController diseaseTemplateController;

    @Autowired
    private ObsService obsService;

    @Before
    public void setUp() throws Exception {
        executeDataSet("obsTestData.xml");
    }

    @Test
    public void shouldReturnObsForAllDiseaseTemplatesWithIntakeAndProgressFromTheLatestVisit() throws Exception {
        List<DiseaseTemplate> diseaseTemplates = deserialize(handle(newGetRequest("/rest/v1/bahmnicore/diseaseTemplates", new Parameter("patientUuid", "86526ed5-3c11-11de-a0ba-001e378eb67a"))), new TypeReference<List<DiseaseTemplate>>() {});
        assertNotNull(diseaseTemplates);
        assertEquals(1, diseaseTemplates.size());
        DiseaseTemplate breastCancer = diseaseTemplates.get(0);
        assertEquals(1, breastCancer.getObservationTemplates().size());
        ObservationTemplate breastCancerIntake = breastCancer.getObservationTemplates().get(0);
        assertEquals(4, breastCancerIntake.getBahmniObservations().size());
        assertEquals("Breast Cancer Intake", breastCancerIntake.getConcept().getName());
        assertEquals("BC_intake_concept_uuid", breastCancerIntake.getConcept().getUuid());
    }

    @Test
    public void shouldReturnObsForADiseaseTemplateWithIntakeAndProgressAcrossAllVisits() throws Exception {
        List<DiseaseTemplate> diseaseTemplates = deserialize(handle(newGetRequest("/rest/v1/bahmnicore/diseaseTemplates", new Parameter("patientUuid", "86526ed5-3c11-11de-a0ba-001e378eb67a"))), new TypeReference<List<DiseaseTemplate>>() {});

    }
}