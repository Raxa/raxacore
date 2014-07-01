package org.bahmni.module.bahmnicore.service.impl;

import org.bahmni.module.bahmnicore.model.BahmniVisit.LabOrderResult;
import org.bahmni.module.bahmnicore.model.BahmniVisit.LabOrderResults;
import org.junit.Test;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class LabOrderResultsServiceIT extends BaseModuleWebContextSensitiveTest {

    @Autowired
    private LabOrderResultsService labOrderResultsService;

    @Test
    public void shouldMapTestOrdersAndResults() throws Exception {
        executeDataSet("diagnosisMetadata.xml");
        executeDataSet("dispositionMetadata.xml");
        executeDataSet("labOrderTestData.xml");
        Patient patient = Context.getPatientService().getPatient(1);

        LabOrderResults results = labOrderResultsService.getAll(patient);
        List<LabOrderResult> labOrderResults = results.getResults();

        assertNotNull(labOrderResults);
        assertEquals(4, labOrderResults.size());

        assertOrderPresent(labOrderResults, "Haemoglobin", "Blood Panel", 16, "99.0", 200.0, 300.0, true, null);
        assertOrderPresent(labOrderResults, "ESR", "Blood Panel", 16, "10.0", null, null, false, "Some Notes");
        assertOrderPresent(labOrderResults, "Urea Nitrogen", null, 16, "20.0", null, null, null, null);
        assertOrderPresent(labOrderResults, "HIV ELISA", null, 16, null, null, null, null, null);
    }

    private void assertOrderPresent(List<LabOrderResult> labOrderResults, String testName, String panelName, Integer accessionEncounterId, String value, Double minNormal, Double maxNormal, Boolean abnormal, String notes) {
        Encounter accessionEncounter = Context.getEncounterService().getEncounter(accessionEncounterId);
        for (LabOrderResult labOrderResult : labOrderResults) {
            if(labOrderResult.getTestName().equals(testName)) {
                assertEquals(panelName, labOrderResult.getPanelName());
                assertEquals(accessionEncounter.getUuid(), labOrderResult.getAccessionUuid());
                assertEquals(accessionEncounter.getEncounterDatetime(), labOrderResult.getAccessionDateTime());
                assertEquals(value, labOrderResult.getResult());
                assertEquals(minNormal, labOrderResult.getMinNormal());
                assertEquals(maxNormal, labOrderResult.getMaxNormal());
                assertEquals(abnormal, labOrderResult.getAbnormal());
                assertEquals(notes, labOrderResult.getNotes());
                return;
            }
        }
        fail();
    }
}
