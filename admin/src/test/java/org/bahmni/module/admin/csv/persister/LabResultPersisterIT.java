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
import org.openmrs.module.bahmniemrapi.laborder.contract.LabOrderResult;
import org.openmrs.module.bahmniemrapi.laborder.service.LabOrderResultsService;
import org.openmrs.test.TestUtil;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static org.junit.Assert.*;

@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class LabResultPersisterIT extends BaseModuleWebContextSensitiveTest {
    @Autowired
    private VisitService visitService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private LabResultPersister labResultPersister;

    @Autowired
    private LabOrderResultsService labOrderResultsService;
    private UserContext userContext;

    @Before
    public void setUp() throws Exception {
        executeDataSet("baseMetaData.xml");
        executeDataSet("diagnosisMetaData.xml");
        executeDataSet("dispositionMetaData.xml");
        executeDataSet("labResultMetaData.xml");
        executeDataSet("labResult.xml");
        Context.authenticate("admin", "test");
        userContext = Context.getUserContext();
        labResultPersister.init(userContext, null, true);
    }

    @Test
    public void test_persist() throws Exception {
        String visitType = "LAB RESULT IMPORT VISIT";
        LabResultsRow labResultsRow = new LabResultsRow();
        labResultsRow.setPatientIdentifier("GAN200001").setTestDateString("2014-10-11").setVisitType(visitType);
        labResultsRow.setTestResults(Arrays.asList(new LabResultRow().setTest("Urea Nitorgen").setResult("10")));

        RowResult<LabResultsRow> rowResult = labResultPersister.persist(labResultsRow);

        Patient patient = patientService.getPatientByUuid("75e04d42-3ca8-11e3-bf2b-ab87271c1b75");
        List<Visit> visits = visitService.getVisitsByPatient(patient);
        assertTrue(rowResult.isSuccessful());
        // Assert visit data
        assertEquals(1, visits.size());
        Visit visit = visits.get(0);
        assertEquals(visitType, visit.getVisitType().getName());
        assertEquals(1, visit.getEncounters().size());
        assertEquals(TestUtil.createDateTime("2014-10-11"), visit.getStartDatetime());
        assertEquals(TestUtil.createDateTime("2014-10-11 23:59:59"), visit.getStopDatetime());
        // Assert encounter data
        Encounter encounter = visit.getEncounters().iterator().next();
        assertEquals(1, encounter.getEncounterProviders().size());
        assertEquals(LabResultPersister.LAB_RESULT_ENCOUNTER_TYPE, encounter.getEncounterType().getName());
        assertEquals(TestUtil.createDateTime("2014-10-11"), encounter.getEncounterDatetime());
        assertEquals(userContext.getAuthenticatedUser().getId(), encounter.getProvider().getId());
        // Assert tests orders data
        assertEquals(1, encounter.getOrders().size());
        Order order = encounter.getOrders().iterator().next();
        assertEquals("Urea Nitorgen", order.getConcept().getName().getName());
        assertEquals(userContext.getAuthenticatedUser().getId(), order.getOrderer().getId());
        assertEquals(TestUtil.createDateTime("2014-10-11"), order.getDateActivated());
        assertEquals(TestUtil.createDateTime("2014-10-11 23:59:59"), order.getAutoExpireDate());
        assertEquals(LabResultPersister.LAB_ORDER_TYPE, order.getOrderType().getName());
        assertEquals(CareSetting.CareSettingType.OUTPATIENT.name(), order.getCareSetting().getName());
        // Assert results data
        List<LabOrderResult> labOrderResults = labOrderResultsService.getAll(patient, visits).getResults();
        assertEquals(1, labOrderResults.size());
        LabOrderResult labOrderResult = labOrderResults.get(0);
        assertEquals("Urea Nitorgen", labOrderResult.getTestName());
        assertEquals("10.0", labOrderResult.getResult());
        assertEquals(TestUtil.createDateTime("2014-10-11"), labOrderResult.getResultDateTime());
    }
}