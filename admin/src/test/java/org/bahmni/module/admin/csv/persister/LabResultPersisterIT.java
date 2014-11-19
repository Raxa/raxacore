package org.bahmni.module.admin.csv.persister;

import org.bahmni.csv.RowResult;
import org.bahmni.module.admin.csv.models.LabResultRow;
import org.bahmni.module.admin.csv.models.LabResultsRow;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.*;
import org.openmrs.api.PatientService;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.UserContext;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class LabResultPersisterIT extends BaseModuleWebContextSensitiveTest {
    @Autowired
    private VisitService visitService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private LabResultPersister labResultPersister;
    private UserContext userContext;

    @Before
    public void setUp() throws Exception {
        executeDataSet("labResultMetaData.xml");
        executeDataSet("labResult.xml");
        Context.authenticate("admin", "test");
        userContext = Context.getUserContext();
        labResultPersister.init(userContext, null, true);
    }

    @Test
    public void testPersist() {
        LabResultsRow labResultsRow = new LabResultsRow();
        labResultsRow.setPatientIdentifier("GAN200001");
        labResultsRow.setTestDateString("2014-10-11");
        LabResultRow labResultRow = new LabResultRow();
        labResultRow.setTest("Urea Nitorgen");
        labResultRow.setResult("10");
        List<LabResultRow> labResultRows = new ArrayList<>();
        labResultRows.add(labResultRow);
        labResultsRow.setTestResults(labResultRows);
        String visitType = "LAB RESULT IMPORT VISIT";
        labResultsRow.setVisitType(visitType);

        RowResult<LabResultsRow> rowResult = labResultPersister.persist(labResultsRow);

        Patient patient = patientService.getPatientByUuid("75e04d42-3ca8-11e3-bf2b-ab87271c1b75");
        List<Visit> visits = visitService.getVisitsByPatient(patient);
        assertTrue(rowResult.isSuccessful());
        assertEquals(1, visits.size());
        assertEquals(visitType, visits.get(0).getVisitType().getName());
        assertEquals(1, visits.get(0).getEncounters().size());
        Encounter encounter = visits.get(0).getEncounters().iterator().next();
        Set<Obs> obs = encounter.getObs();
        Set<Order> orders = encounter.getOrders();
        assertEquals(1, encounter.getEncounterProviders().size());
        assertEquals(userContext.getAuthenticatedUser().getId(), encounter.getProvider().getId());
        assertEquals(1, orders.size());
        assertEquals(1, obs.size());
    }
}