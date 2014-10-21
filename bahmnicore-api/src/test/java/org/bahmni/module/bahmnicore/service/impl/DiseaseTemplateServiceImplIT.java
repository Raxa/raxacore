package org.bahmni.module.bahmnicore.service.impl;

import org.bahmni.module.bahmnicore.contract.diseasetemplate.DiseaseTemplate;
import org.bahmni.module.bahmnicore.contract.diseasetemplate.ObservationTemplate;
import org.bahmni.module.bahmnicore.service.DiseaseTemplateService;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.VisitService;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniObservation;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class DiseaseTemplateServiceImplIT extends BaseModuleWebContextSensitiveTest {
    @Before
    public void setUp() throws Exception {
        executeDataSet("diseaseTemplate.xml");
    }

    @Autowired
    private DiseaseTemplateService diseaseTemplateService;

    @Test
    public void get_disease_template_for_observation_template_concept() throws Exception {
        DiseaseTemplate diseaseTemplate = diseaseTemplateService.diseaseTemplateFor("b2a59310-58e8-11e4-8ed6-0800200c9a66", "Blood Pressure");
        assertEquals(1, diseaseTemplate.getObservationTemplates().size());
        ObservationTemplate observationTemplate = diseaseTemplate.getObservationTemplates().get(0);
        assertEquals(1, observationTemplate.getBahmniObservations().size());
        BahmniObservation obs = observationTemplate.getBahmniObservations().get(0);
        assertTrue(obs.getValue().equals(100.0));
    }
}