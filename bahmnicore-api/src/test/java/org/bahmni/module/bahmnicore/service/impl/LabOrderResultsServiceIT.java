package org.bahmni.module.bahmnicore.service.impl;

import org.bahmni.module.bahmnicore.model.BahmniVisit.LabOrderResult;
import org.bahmni.module.bahmnicore.model.BahmniVisit.LabOrderResults;
import org.junit.Test;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class LabOrderResultsServiceIT extends BaseModuleWebContextSensitiveTest {

    @Autowired
    private LabOrderResultsService labOrderResultsService;

    @Test
    public void shouldMapTestOrdersAndResultsForAllVisits() throws Exception {
        executeDataSet("diagnosisMetadata.xml");
        executeDataSet("dispositionMetadata.xml");
        executeDataSet("labOrderTestData.xml");
        Patient patient = Context.getPatientService().getPatient(1);

        LabOrderResults results = labOrderResultsService.getAll(patient, null);
        List<LabOrderResult> labOrderResults = results.getResults();

        assertNotNull(labOrderResults);
        assertEquals(6, labOrderResults.size());

        assertOrderPresent(labOrderResults, "Haemoglobin", "Blood Panel", 16, "System OpenMRS", "99.0", 200.0, 300.0, true, null, false);
        assertOrderPresent(labOrderResults, "ESR", "Blood Panel", 16, "System OpenMRS", "10.0", null, null, false, "Some Notes", false);
        assertOrderPresent(labOrderResults, "Urea Nitrogen", null, 16, "System OpenMRS", "20.0", null, null, null, null, false);
        assertOrderPresent(labOrderResults, "HIV ELISA", null, 16, null, null, null, null, null, null, false);
        assertOrderPresent(labOrderResults, "PS for Malaria", null, 16, "System OpenMRS", null, null, null, null, null, true);
        assertOrderPresent(labOrderResults, "PS for Malaria", null, 17, "System OpenMRS", "Result for PS Malaria", null, null, null, "Not good", false);
    }

    @Test
    public void shouldMapTestOrdersAndResultsForGivenVisit() throws Exception {
        executeDataSet("diagnosisMetadata.xml");
        executeDataSet("dispositionMetadata.xml");
        executeDataSet("labOrderTestData.xml");
        Patient patient = Context.getPatientService().getPatient(1);
        Visit visit = Context.getVisitService().getVisit(4);

        LabOrderResults results = labOrderResultsService.getAll(patient, Arrays.asList(visit));
        List<LabOrderResult> labOrderResults = results.getResults();

        assertNotNull(labOrderResults);
        assertEquals(1, labOrderResults.size());

        assertOrderPresent(labOrderResults, "PS for Malaria", null, 17, "System OpenMRS", "Result for PS Malaria", null, null, null, "Not good", false);
    }

    private void assertOrderPresent(List<LabOrderResult> labOrderResults, String testName, String panelName, Integer accessionEncounterId, String provider, String value, Double minNormal, Double maxNormal, Boolean abnormal, String notes, Boolean referredOut) {
        Encounter accessionEncounter = Context.getEncounterService().getEncounter(accessionEncounterId);
        for (LabOrderResult labOrderResult : labOrderResults) {
            if(labOrderResult.getTestName().equals(testName) && labOrderResult.getAccessionUuid().equals(accessionEncounter.getUuid())) {
                assertEquals(panelName, labOrderResult.getPanelName());
                assertEquals(accessionEncounter.getEncounterDatetime(), labOrderResult.getAccessionDateTime());
                assertEquals(value, labOrderResult.getResult());
                assertEquals(minNormal, labOrderResult.getMinNormal());
                assertEquals(maxNormal, labOrderResult.getMaxNormal());
                assertEquals(abnormal, labOrderResult.getAbnormal());
                assertEquals(notes, labOrderResult.getNotes());
                assertEquals(referredOut, labOrderResult.getReferredOut());
                assertEquals(provider, labOrderResult.getProvider());
                return;
            }
        }
        fail();
    }
}
