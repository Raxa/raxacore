package org.bahmni.module.admin.csv;

import org.bahmni.csv.KeyValue;
import org.bahmni.csv.RowResult;
import org.bahmni.module.admin.csv.models.EncounterRow;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.EncounterService;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.UserContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class EncounterPersisterIT extends BaseModuleContextSensitiveTest {
    @Autowired
    private EncounterPersister encounterPersister;

    @Autowired
    private EncounterService encounterService;

    @Autowired
    private VisitService visitService;

    private static final SimpleDateFormat observationDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private String path;
    protected UserContext userContext;

    @Before
    public void setUp() throws Exception {
        executeDataSet("baseMetaData.xml");
        executeDataSet("diagnosisMetaData.xml");
        executeDataSet("dispositionMetaData.xml");
        executeDataSet("dataSetup.xml");
        path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        System.setProperty("OPENMRS_APPLICATION_DATA_DIRECTORY", path);


        Context.authenticate("admin", "test");
        userContext = Context.getUserContext();
        encounterPersister.init(userContext, null);
    }

    @Test
    public void fail_validation_for_empty_encounter_type() throws Exception {
        EncounterRow encounterRow = new EncounterRow();
        RowResult<EncounterRow> validationResult = encounterPersister.validate(encounterRow);
        assertTrue("Empty Encounter Type", validationResult.getErrorMessage().contains("Empty Encounter Type"));
    }

    @Test
    public void fail_validation_for_encounter_type_not_found() throws Exception {
        EncounterRow encounterRow = new EncounterRow();
        encounterRow.encounterType = "INVALID ENCOUNTER TYPE";
        encounterRow.visitType = "OPD";
        RowResult<EncounterRow> validationResult = encounterPersister.validate(encounterRow);
        assertTrue("Invalid Encounter Type not found", validationResult.getErrorMessage().contains("Encounter Type 'INVALID ENCOUNTER TYPE' not found"));
    }

    @Test
    public void fail_validation_for_visit_type_not_found() throws Exception {
        EncounterRow encounterRow = new EncounterRow();
        encounterRow.encounterType = "OPD";
        encounterRow.visitType = "INVALID VISIT TYPE";
        RowResult<EncounterRow> validationResult = encounterPersister.validate(encounterRow);
        assertTrue("Invalid Visit Type not found", validationResult.getErrorMessage().contains("Visit Type 'INVALID VISIT TYPE' not found"));
    }

    @Test
    public void fail_validation_for_empty_visit_type() throws Exception {
        EncounterRow encounterRow = new EncounterRow();
        encounterRow.encounterType = "OPD";
        RowResult<EncounterRow> validationResult = encounterPersister.validate(encounterRow);
        assertTrue("Visit Type null not found", validationResult.getErrorMessage().contains("Empty Visit Type"));
    }

    @Test
    public void fail_validation_for_encounter_date_in_incorrect_format() throws Exception {
        EncounterRow encounterRow = new EncounterRow();
        encounterRow.encounterType = "OPD";
        encounterRow.encounterDateTime = "1977/08/23";
        encounterRow.visitType = "OPD";
        RowResult<EncounterRow> validationResult = encounterPersister.validate(encounterRow);
        assertTrue("Encounter date time is required and should be 'dd/mm/yyyy' format", validationResult.getErrorMessage().contains("Encounter date time is required and should be 'dd/mm/yyyy' format"));
    }

    @Test
    public void pass_validation_for_correct_entries() throws Exception {
        EncounterRow encounterRow = new EncounterRow();
        encounterRow.encounterType = "OPD";
        encounterRow.visitType = "OPD";
        encounterRow.patientIdentifier = "GAN200000";
        encounterRow.encounterDateTime = "23/08/1977";

        RowResult<EncounterRow> validationResult = encounterPersister.validate(encounterRow);
        assertEquals("", validationResult.getErrorMessage());
    }

    @Test
    public void pass_validation_and_persist_encounters_for_patient() throws Exception {
        EncounterRow encounterRow = new EncounterRow();
        encounterRow.encounterDateTime = "11/11/1111";
        encounterRow.encounterType = "OPD";
        encounterRow.visitType = "OPD";
        encounterRow.patientIdentifier = "GAN200000";
        RowResult<EncounterRow> validationResult = encounterPersister.validate(encounterRow);
        assertEquals("", validationResult.getErrorMessage());

        RowResult<EncounterRow> persistenceResult = encounterPersister.persist(encounterRow);
        assertNull(persistenceResult.getErrorMessage());

        Context.openSession();
        Context.authenticate("admin", "test");
        List<Encounter> encounters = encounterService.getEncountersByPatientIdentifier(encounterRow.patientIdentifier);
        Context.flushSession();
        Context.closeSession();

        Encounter encounter = encounters.get(0);
        assertEquals(1, encounters.size());
        assertEquals("Anad", encounter.getPatient().getGivenName());
        assertEquals("Kewat", encounter.getPatient().getFamilyName());
        assertEquals("OPD", encounter.getVisit().getVisitType().getName());
        assertEquals("OPD", encounter.getEncounterType().getName());

        Date encounterDatetime = encounter.getEncounterDatetime();
        assertEquals("11/11/1111", observationDateFormat.format(encounterDatetime));
    }

    @Test
    public void pass_validation_and_persist_encounter_and_observations_for_patient() throws Exception {
        EncounterRow encounterRow = new EncounterRow();
        encounterRow.encounterType = "OPD";
        encounterRow.visitType = "OPD";
        encounterRow.patientIdentifier = "GAN200000";
        encounterRow.encounterDateTime = "11/11/1111";
        encounterRow.obsRows = new ArrayList<>();
        KeyValue weight = new KeyValue("WEIGHT", "150");
        encounterRow.obsRows.add(weight);
        RowResult<EncounterRow> validationResult = encounterPersister.validate(encounterRow);
        assertEquals("", validationResult.getErrorMessage());

        RowResult<EncounterRow> persistenceResult = encounterPersister.persist(encounterRow);
        assertNull(persistenceResult.getErrorMessage());

        Context.openSession();
        Context.authenticate("admin", "test");
        List<Encounter> encounters = encounterService.getEncountersByPatientIdentifier(encounterRow.patientIdentifier);
        Context.closeSession();
        Encounter encounter = encounters.get(0);
        assertEquals(1, encounters.size());
        assertEquals("Anad", encounter.getPatient().getGivenName());
        assertEquals("Kewat", encounter.getPatient().getFamilyName());
        assertEquals("OPD", encounter.getVisit().getVisitType().getName());
        assertEquals("OPD", encounter.getEncounterType().getName());
        assertEquals(1, encounter.getAllObs().size());
        assertEquals("WEIGHT", encounter.getAllObs().iterator().next().getConcept().getName().getName());
        Date obsDatetime = encounter.getAllObs().iterator().next().getObsDatetime();
        assertEquals("11/11/1111", observationDateFormat.format(obsDatetime));
        assertEquals("150.0", encounter.getAllObs().iterator().next().getValueAsString(Context.getLocale()));
    }

    @Test
    public void roll_back_transaction_once_persistence_fails_for_one_resource() throws Exception {
        EncounterRow encounterRow = new EncounterRow();
        encounterRow.encounterType = "OPD";
        encounterRow.visitType = "OPD";
        encounterRow.patientIdentifier = "GAN200000";
        encounterRow.obsRows = new ArrayList<>();
        KeyValue weight = new KeyValue("WEIGHT", "150");
        encounterRow.obsRows.add(weight);
        encounterPersister.validate(encounterRow);
        encounterRow.encounterType = "O1PD";
        encounterPersister.persist(encounterRow);
        Context.openSession();
        Context.authenticate("admin", "test");
        List<Encounter> encounters = encounterService.getEncountersByPatientIdentifier(encounterRow.patientIdentifier);
        List<Visit> visits = visitService.getVisitsByPatient(new Patient(1));
        Context.closeSession();
        assertEquals(0, visits.size());
        assertEquals(0, encounters.size());
    }

    @Test
    public void persist_diagnosis() throws Exception {
        EncounterRow encounterRow = new EncounterRow();
        encounterRow.encounterType = "OPD";
        encounterRow.visitType = "OPD";
        encounterRow.patientIdentifier = "GAN200000";
        encounterRow.encounterDateTime = "11/11/1111";
        encounterRow.obsRows = new ArrayList<>();
        encounterRow.diagnosesRows = new ArrayList<>();
        KeyValue weight = new KeyValue("WEIGHT", "150");
        KeyValue diabetes = new KeyValue("Diagnosis1", "Diabetes");
        encounterRow.obsRows.add(weight);
        encounterRow.diagnosesRows.add(diabetes);

        RowResult<EncounterRow> validationResult = encounterPersister.validate(encounterRow);
        assertEquals("", validationResult.getErrorMessage());

        RowResult<EncounterRow> persistenceResult = encounterPersister.persist(encounterRow);
        assertNull(persistenceResult.getErrorMessage());

        Context.openSession();
        Context.authenticate("admin", "test");
        List<Encounter> encounters = encounterService.getEncountersByPatientIdentifier(encounterRow.patientIdentifier);
        Context.closeSession();

        Encounter encounter = encounters.get(0);
        assertEquals(1, encounters.size());
        assertEquals("Anad", encounter.getPatient().getGivenName());
        assertEquals("Kewat", encounter.getPatient().getFamilyName());
        assertEquals("OPD", encounter.getVisit().getVisitType().getName());
        assertEquals("OPD", encounter.getEncounterType().getName());

        List<Obs> allObs = new ArrayList<>();
        allObs.addAll(encounter.getAllObs());
        assertEquals(2, allObs.size());

        int weightIndex = 0;
        int diagnosisIndex = 0;
        if (allObs.get(0).getGroupMembers() == null || allObs.get(0).getGroupMembers().size() == 0) {
            diagnosisIndex = 1;
        } else {
            weightIndex = 1;
        }
        Obs weightObs = allObs.get(weightIndex);
        Obs diagnosisObs = allObs.get(diagnosisIndex);
        assertEquals("WEIGHT", weightObs.getConcept().getName().getName());
        assertEquals("150.0", weightObs.getValueAsString(Context.getLocale()));
        assertEquals("Diagnosis Concept Set", diagnosisObs.getConcept().getName().getName());
        assertEquals("11/11/1111", observationDateFormat.format(diagnosisObs.getObsDatetime()));

        List<String> obsConceptNames = new ArrayList<>();
        for (Obs obs : diagnosisObs.getGroupMembers()) {
            obsConceptNames.add(obs.getConcept().getName().getName());
        }
        assertTrue(obsConceptNames.contains("Diabetes"));
        assertTrue(obsConceptNames.contains("Diagnosis Certainty"));
        assertTrue(obsConceptNames.contains("Diagnosis Order"));
        assertTrue(obsConceptNames.contains("Bahmni Diagnosis Status"));
        assertTrue(obsConceptNames.contains("Bahmni Diagnosis Revised"));
        assertTrue(obsConceptNames.contains("Bahmni Initial Diagnosis"));
    }

    @Test
    public void throw_error_when_patient_not_found() throws Exception {
        EncounterRow encounterRow = new EncounterRow();
        encounterRow.encounterDateTime = "11/11/1111";
        encounterRow.encounterType = "OPD";
        encounterRow.visitType = "OPD";
        encounterRow.patientIdentifier = "GAN200001";
        encounterPersister.init(userContext, "NoMatch.groovy");

        RowResult<EncounterRow> persistenceResult = encounterPersister.persist(encounterRow);
        assertNotNull(persistenceResult.getErrorMessage());
        assertEquals("Patient not found. Patient Id : 'GAN200001'", persistenceResult.getErrorMessage());
    }

    @Test
    public void throw_error_when_multiple_patients_found() throws Exception {
        EncounterRow encounterRow = new EncounterRow();
        encounterRow.encounterDateTime = "11/11/1111";
        encounterRow.encounterType = "OPD";
        encounterRow.visitType = "OPD";
        encounterRow.patientIdentifier = "200000";
        encounterPersister.init(userContext, "MultipleMatchPatient.groovy");

        RowResult<EncounterRow> persistenceResult = encounterPersister.persist(encounterRow);

        assertTrue(persistenceResult.getErrorMessage().contains("GAN200000, SEM200000"));
    }

    @Test
    public void external_algorithm_should_return_only_patients_with_GAN_identifier() throws Exception {
        String patientId = "200000";

        EncounterRow encounterRow = new EncounterRow();
        encounterRow.encounterDateTime = "11/11/1111";
        encounterRow.encounterType = "OPD";
        encounterRow.visitType = "OPD";
        encounterRow.patientIdentifier = patientId;
        encounterPersister.init(userContext, "GANIdentifier.groovy");

        RowResult<EncounterRow> persistenceResult = encounterPersister.persist(encounterRow);
        assertNull(persistenceResult.getErrorMessage());
        Context.openSession();
        Context.authenticate("admin", "test");

        encounterRow.patientIdentifier = "GAN" + patientId;
        List<Encounter> encounters = encounterService.getEncountersByPatientIdentifier(encounterRow.patientIdentifier);
        Context.closeSession();
        assertEquals(1, encounters.size());
    }

    @Test
    public void external_algorithm_returns_patients_matching_id_and_name() throws Exception {
        EncounterRow encounterRow = new EncounterRow();
        encounterRow.encounterDateTime = "11/11/1111";
        encounterRow.encounterType = "OPD";
        encounterRow.visitType = "OPD";
        encounterRow.patientIdentifier = "GAN200000";
        encounterRow.patientAttributes = getPatientAttributes();
        encounterPersister.init(userContext, "IdAndNameMatch.groovy");

        RowResult<EncounterRow> persistenceResult = encounterPersister.persist(encounterRow);
        assertNull(persistenceResult.getErrorMessage());
        Context.openSession();
        Context.authenticate("admin", "test");

        List<Encounter> encounters = encounterService.getEncountersByPatientIdentifier(encounterRow.patientIdentifier);
        Context.closeSession();
        assertEquals(1, encounters.size());
    }

    private List<KeyValue> getPatientAttributes() {
        List<KeyValue> patientAttributes = new ArrayList<>();
        patientAttributes.add(new KeyValue("given_name", "Anad"));
        return patientAttributes;
    }
}