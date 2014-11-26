package org.bahmni.module.admin.csv.persister;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.bahmni.csv.KeyValue;
import org.bahmni.csv.Messages;
import org.bahmni.module.admin.csv.models.EncounterRow;
import org.bahmni.module.admin.csv.models.MultipleEncounterRow;
import org.bahmni.module.admin.csv.utils.CSVUtils;
import static org.hamcrest.CoreMatchers.is;
import org.hamcrest.Matchers;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.Visit;
import org.openmrs.api.EncounterService;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.UserContext;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class EncounterPersisterIT extends BaseModuleWebContextSensitiveTest {
    @Autowired
    private EncounterPersister encounterPersister;

    @Autowired
    private EncounterService encounterService;

    @Autowired
    private VisitService visitService;

    private String path;
    protected UserContext userContext;
    private boolean shouldMatchExactPatientId = false;

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
        boolean shouldMatchExactPatientId = false;
        encounterPersister.init(userContext, null, shouldMatchExactPatientId);
    }

    @Test
    public void fail_validation_for_empty_encounter_type() throws Exception {
        MultipleEncounterRow multipleEncounterRow = new MultipleEncounterRow();
        Messages rowResult = encounterPersister.persist(multipleEncounterRow);
        assertTrue("No Encounter details. Should have failed", !rowResult.isEmpty());
    }

    @Test
    public void fail_validation_for_encounter_type_not_found() throws Exception {
        MultipleEncounterRow multipleEncounterRow = new MultipleEncounterRow();
        multipleEncounterRow.encounterType = "INVALID ENCOUNTER TYPE";
        multipleEncounterRow.visitType = "OPD";
        multipleEncounterRow.patientIdentifier = "GAN200000";

        EncounterRow anEncounter = new EncounterRow();
        anEncounter.encounterDateTime = "11-11-1111";

        multipleEncounterRow.encounterRows = new ArrayList<>();
        multipleEncounterRow.encounterRows.add(anEncounter);

        Messages validationErrors = encounterPersister.persist(multipleEncounterRow);
        assertTrue("Invalid Encounter Type not found. Error Message:",
                validationErrors.toString().contains("Encounter type:'INVALID ENCOUNTER TYPE' not found"));
    }

    @Test
    public void fail_validation_for_visit_type_not_found() throws Exception {
        MultipleEncounterRow multipleEncounterRow = new MultipleEncounterRow();
        multipleEncounterRow.encounterType = "Consultation";
        multipleEncounterRow.visitType = "INVALID VISIT TYPE";
        multipleEncounterRow.patientIdentifier = "GAN200000";

        EncounterRow anEncounter = new EncounterRow();
        anEncounter.obsRows = new ArrayList<>();
        anEncounter.obsRows.add(new KeyValue("WEIGHT", "150"));
        anEncounter.encounterDateTime = "1111-11-11";

        multipleEncounterRow.encounterRows = new ArrayList<>();
        multipleEncounterRow.encounterRows.add(anEncounter);

        Messages validationErrors = encounterPersister.persist(multipleEncounterRow);
        assertTrue("Invalid Visit Type not found. Error Message:" + validationErrors.toString(),
                validationErrors.toString().contains("Visit type:'INVALID VISIT TYPE' not found"));
    }

    @Test
    public void fail_validation_for_empty_visit_type() throws Exception {
        MultipleEncounterRow multipleEncounterRow = new MultipleEncounterRow();
        multipleEncounterRow.encounterType = "Consultation";
        multipleEncounterRow.patientIdentifier = "GAN200000";

        EncounterRow anEncounter = new EncounterRow();
        anEncounter.encounterDateTime = "1111-11-11";

        multipleEncounterRow.encounterRows = new ArrayList<>();
        multipleEncounterRow.encounterRows.add(anEncounter);

        Messages validationErrors = encounterPersister.persist(multipleEncounterRow);
        assertTrue("Visit Type null not found. Error Message:" + validationErrors.toString(),
                validationErrors.toString().contains("Visit type:'null' not found"));
    }

    @Test
    public void fail_validation_for_encounter_date_in_incorrect_format() throws Exception {
        MultipleEncounterRow multipleEncounterRow = new MultipleEncounterRow();
        multipleEncounterRow.encounterType = "Consultation";
        multipleEncounterRow.patientIdentifier = "GAN200000";
        multipleEncounterRow.visitType = "OPD";

        EncounterRow anEncounter = new EncounterRow();
        anEncounter.encounterDateTime = "1977/08/23";

        multipleEncounterRow.encounterRows = new ArrayList<>();
        multipleEncounterRow.encounterRows.add(anEncounter);

        Messages validationErrors = encounterPersister.persist(multipleEncounterRow);
        assertTrue("Encounter date time is required and should be 'dd/mm/yyyy' format.. Error Message:" + validationErrors.toString(),
                validationErrors.toString().contains("Unparseable date: \"1977/08/23\""));
    }

    @Test
    public void no_validation_for_encounters() {
        Messages validationErrors = encounterPersister.validate(new MultipleEncounterRow());
        assertTrue("No Validation failure. Encounter Import does not run validation stage", validationErrors.isEmpty());
    }

    @Test
    public void persist_encounters_for_patient() throws Exception {
        MultipleEncounterRow multipleEncounterRow = new MultipleEncounterRow();
        multipleEncounterRow.encounterType = "Consultation";
        multipleEncounterRow.visitType = "OPD";
        multipleEncounterRow.patientIdentifier = "GAN200000";

        EncounterRow anEncounter = new EncounterRow();
        anEncounter.obsRows = new ArrayList<>();
        anEncounter.obsRows.add(new KeyValue("WEIGHT", "150"));
        anEncounter.encounterDateTime = "1111-11-11";

        multipleEncounterRow.encounterRows = new ArrayList<>();
        multipleEncounterRow.encounterRows.add(anEncounter);

        Messages errorMessages = encounterPersister.persist(multipleEncounterRow);
        assertTrue("Should have persisted the encounter row.", errorMessages.isEmpty());

        Context.openSession();
        Context.authenticate("admin", "test");
        List<Encounter> encounters = encounterService.getEncountersByPatientIdentifier(multipleEncounterRow.patientIdentifier);
        Context.flushSession();
        Context.closeSession();

        assertEquals(1, encounters.size());

        Encounter encounter = encounters.get(0);
        assertEquals("Anad", encounter.getPatient().getGivenName());
        assertEquals("Kewat", encounter.getPatient().getFamilyName());
        assertEquals("OPD", encounter.getVisit().getVisitType().getName());
        assertEquals("Consultation", encounter.getEncounterType().getName());

        Date encounterDatetime = encounter.getEncounterDatetime();
        assertEquals("1111-11-11", new SimpleDateFormat(CSVUtils.ENCOUNTER_DATE_PATTERN).format(encounterDatetime));
        }

    @Test
    public void create_visit_as_per_dates_in_file() throws Exception {
        String registrationNumber = "GAN200000";
        String visitStartDate = "2011-11-11";
        String visitEndDate = "2011-12-11";

        MultipleEncounterRow multipleEncounterRow = new MultipleEncounterRow();
        multipleEncounterRow.encounterType = "Consultation";
        multipleEncounterRow.visitType = "OPD";
        multipleEncounterRow.patientIdentifier = registrationNumber;
        multipleEncounterRow.visitStartDate = visitStartDate;
        multipleEncounterRow.visitEndDate = visitEndDate;

        EncounterRow anEncounter = new EncounterRow();
        anEncounter.obsRows = new ArrayList<>();
        anEncounter.obsRows.add(new KeyValue("WEIGHT", "150"));
        anEncounter.encounterDateTime = "2011-12-1";

        multipleEncounterRow.encounterRows = new ArrayList<>();
        multipleEncounterRow.encounterRows.add(anEncounter);

        Messages errorMessages = encounterPersister.persist(multipleEncounterRow);
        assertTrue("Should have persisted the encounter row.", errorMessages.isEmpty());

        Context.openSession();
        Context.authenticate("admin", "test");

        Patient patient = new Patient();
        PatientIdentifier patientIdentifier = new PatientIdentifier();
        patientIdentifier.setIdentifier(registrationNumber);
        patient.addIdentifier(patientIdentifier);

        List<Encounter> encounters = encounterService.getEncountersByPatientIdentifier(multipleEncounterRow.patientIdentifier);
        Context.flushSession();
        Context.closeSession();

        assertEquals(1, encounters.size());
        Visit newlyCreatedVisit = encounters.get(0).getVisit();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(CSVUtils.ENCOUNTER_DATE_PATTERN);
        assertEquals(visitStartDate, simpleDateFormat.format(newlyCreatedVisit.getStartDatetime()));
        assertEquals(visitEndDate, simpleDateFormat.format(newlyCreatedVisit.getStopDatetime()));
    }

    @Test
    public void persist_multiple_encounters_for_patient() throws Exception {
        MultipleEncounterRow multipleEncounterRow = new MultipleEncounterRow();
        multipleEncounterRow.encounterType = "Consultation";
        multipleEncounterRow.visitType = "OPD";
        multipleEncounterRow.patientIdentifier = "GAN200000";

        EncounterRow anEncounter = new EncounterRow();
        anEncounter.obsRows = new ArrayList<>();
        anEncounter.obsRows.add(new KeyValue("WEIGHT", "150"));
        anEncounter.encounterDateTime = "1111-11-11";

        EncounterRow anotherEncounter = new EncounterRow();
        anotherEncounter.obsRows = new ArrayList<>();
        anotherEncounter.obsRows.add(new KeyValue("HEIGHT", "75"));
        anotherEncounter.encounterDateTime = "1111-11-12";

        multipleEncounterRow.encounterRows = new ArrayList<>();
        multipleEncounterRow.encounterRows.add(anEncounter);
        multipleEncounterRow.encounterRows.add(anotherEncounter);

        Messages errorMessages = encounterPersister.persist(multipleEncounterRow);
        assertTrue("Should have persisted the encounter row.", errorMessages.isEmpty());

        Context.openSession();
        Context.authenticate("admin", "test");
        List<Encounter> encounters = encounterService.getEncountersByPatientIdentifier(multipleEncounterRow.patientIdentifier);
        Context.flushSession();
        Context.closeSession();

        assertEquals(2, encounters.size());
    }

    @Test
    public void persist_observations_for_patient() throws Exception {
        MultipleEncounterRow multipleEncounterRow = new MultipleEncounterRow();
        multipleEncounterRow.encounterType = "Consultation";
        multipleEncounterRow.visitType = "OPD";
        multipleEncounterRow.patientIdentifier = "GAN200000";

        EncounterRow anEncounter = new EncounterRow();
        anEncounter.obsRows = new ArrayList<>();
        anEncounter.obsRows.add(new KeyValue("WEIGHT", "150"));
        anEncounter.encounterDateTime = "1111-11-11";

        multipleEncounterRow.encounterRows = new ArrayList<>();
        multipleEncounterRow.encounterRows.add(anEncounter);

        Messages errorMessages = encounterPersister.persist(multipleEncounterRow);
        assertTrue("Should have persisted the encounter row.", errorMessages.isEmpty());

        Context.openSession();
        Context.authenticate("admin", "test");
        List<Encounter> encounters = encounterService.getEncountersByPatientIdentifier(multipleEncounterRow.patientIdentifier);
        Context.closeSession();

        Encounter encounter = encounters.get(0);
        assertEquals(1, encounters.size());
        assertEquals("Anad", encounter.getPatient().getGivenName());
        assertEquals("Kewat", encounter.getPatient().getFamilyName());
        assertEquals("OPD", encounter.getVisit().getVisitType().getName());
        assertEquals("Consultation", encounter.getEncounterType().getName());
        assertEquals(1, encounter.getAllObs().size());
        assertEquals("WEIGHT", encounter.getAllObs().iterator().next().getConcept().getName().getName());
        Date obsDatetime = encounter.getAllObs().iterator().next().getObsDatetime();
        assertEquals("1111-11-11", new SimpleDateFormat(CSVUtils.ENCOUNTER_DATE_PATTERN).format(obsDatetime));
        assertEquals("150.0", encounter.getAllObs().iterator().next().getValueAsString(Context.getLocale()));
    }

    @Test
    public void persist_diagnosis() throws Exception {
        MultipleEncounterRow multipleEncounterRow = new MultipleEncounterRow();
        multipleEncounterRow.encounterType = "Consultation";
        multipleEncounterRow.visitType = "OPD";
        multipleEncounterRow.patientIdentifier = "GAN200000";

        EncounterRow anEncounter = new EncounterRow();
        anEncounter.obsRows = new ArrayList<>();
        anEncounter.obsRows.add(new KeyValue("WEIGHT", "150"));
        anEncounter.encounterDateTime = "1111-11-11";
        anEncounter.diagnosesRows = new ArrayList<>();
        anEncounter.diagnosesRows.add(new KeyValue("Diagnosis1", "Diabetes"));

        multipleEncounterRow.encounterRows = new ArrayList<>();
        multipleEncounterRow.encounterRows.add(anEncounter);

        Messages errorMessages = encounterPersister.persist(multipleEncounterRow);
        assertTrue("Should have persisted the encounters.", errorMessages.isEmpty());

        Context.openSession();
        Context.authenticate("admin", "test");
        List<Encounter> encounters = encounterService.getEncountersByPatientIdentifier(multipleEncounterRow.patientIdentifier);
        Context.closeSession();

        Encounter encounter = encounters.get(0);
        assertEquals(1, encounters.size());
        assertEquals("Anad", encounter.getPatient().getGivenName());
        assertEquals("Kewat", encounter.getPatient().getFamilyName());
        assertEquals("OPD", encounter.getVisit().getVisitType().getName());
        assertEquals("Consultation", encounter.getEncounterType().getName());

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
        assertEquals("1111-11-11", new SimpleDateFormat(CSVUtils.ENCOUNTER_DATE_PATTERN).format(diagnosisObs.getObsDatetime()));

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
    public void roll_back_transaction_once_persistence_fails_for_one_resource() throws Exception {
        MultipleEncounterRow multipleEncounterRow = new MultipleEncounterRow();
        multipleEncounterRow.encounterType = "Consultation";
        multipleEncounterRow.visitType = "OPD";
        multipleEncounterRow.patientIdentifier = "GAN200000";
        multipleEncounterRow.encounterRows = new ArrayList<>();

        EncounterRow anEncounter = new EncounterRow();
        anEncounter.obsRows = new ArrayList<>();
        anEncounter.obsRows.add(new KeyValue("WEIGHT", "150"));

        multipleEncounterRow.encounterRows.add(anEncounter);

        multipleEncounterRow.encounterType = "O1PD";
        encounterPersister.persist(multipleEncounterRow);
        Context.openSession();
        Context.authenticate("admin", "test");

        List<Encounter> encounters = encounterService.getEncountersByPatientIdentifier(multipleEncounterRow.patientIdentifier);
        List<Visit> visits = visitService.getVisitsByPatient(new Patient(1));
        Context.closeSession();
        assertEquals(0, visits.size());
        assertEquals(0, encounters.size());
    }

    @Test
    public void throw_error_when_patient_not_found() throws Exception {
        MultipleEncounterRow multipleEncounterRow = new MultipleEncounterRow();
//        multipleEncounterRow.encounterDateTime = "11/11/1111";
        multipleEncounterRow.encounterType = "Consultation";
        multipleEncounterRow.visitType = "OPD";
        multipleEncounterRow.patientIdentifier = "GAN200001";

        encounterPersister.init(userContext, "NoMatch.groovy", shouldMatchExactPatientId);

        Messages errorMessages = encounterPersister.persist(multipleEncounterRow);
        assertThat(errorMessages.size(), is(Matchers.greaterThan(0)));
        assertTrue(errorMessages.toString().contains("No matching patients found with ID:'GAN200001'"));
    }

    @Test
    public void throw_error_when_multiple_patients_found() throws Exception {
        MultipleEncounterRow multipleEncounterRow = new MultipleEncounterRow();
//        multipleEncounterRow.encounterDateTime = "11/11/1111";
        multipleEncounterRow.encounterType = "Consultation";
        multipleEncounterRow.visitType = "OPD";
        multipleEncounterRow.patientIdentifier = "200000";
        encounterPersister.init(userContext, "MultipleMatchPatient.groovy", shouldMatchExactPatientId);

        Messages errorMessages = encounterPersister.persist(multipleEncounterRow);

        assertTrue(errorMessages.toString().contains("GAN200000, SEM200000"));
    }

    @Test
    public void external_algorithm_should_return_only_patients_with_GAN_identifier() throws Exception {
        String patientId = "200000";

        MultipleEncounterRow multipleEncounterRow = new MultipleEncounterRow();
        multipleEncounterRow.encounterType = "Consultation";
        multipleEncounterRow.visitType = "OPD";
        multipleEncounterRow.patientIdentifier = patientId;
        encounterPersister.init(userContext, "GANIdentifier.groovy", shouldMatchExactPatientId);

        EncounterRow anEncounter = new EncounterRow();
        anEncounter.obsRows = new ArrayList<>();
        anEncounter.obsRows.add(new KeyValue("WEIGHT", "150"));
        anEncounter.encounterDateTime = "1111-11-11";

        multipleEncounterRow.encounterRows = new ArrayList<>();
        multipleEncounterRow.encounterRows.add(anEncounter);

        Messages errorMessages = encounterPersister.persist(multipleEncounterRow);

        assertTrue("Should have persisted the encounters.", errorMessages.isEmpty());
        Context.openSession();
        Context.authenticate("admin", "test");

        multipleEncounterRow.patientIdentifier = "GAN" + patientId;
        List<Encounter> encounters = encounterService.getEncountersByPatientIdentifier(multipleEncounterRow.patientIdentifier);
        Context.closeSession();
        assertEquals(1, encounters.size());
    }

    @Test
    public void external_algorithm_returns_patients_matching_id_and_name() throws Exception {
        MultipleEncounterRow multipleEncounterRow = new MultipleEncounterRow();
        multipleEncounterRow.encounterType = "Consultation";
        multipleEncounterRow.visitType = "OPD";
        multipleEncounterRow.patientIdentifier = "GAN200000";
        multipleEncounterRow.patientAttributes = getPatientAttributes();
        encounterPersister.init(userContext, "IdAndNameMatch.groovy", shouldMatchExactPatientId);

        EncounterRow anEncounter = new EncounterRow();
        anEncounter.obsRows = new ArrayList<>();
        anEncounter.obsRows.add(new KeyValue("WEIGHT", "150"));
        anEncounter.encounterDateTime = "1111-11-11";

        multipleEncounterRow.encounterRows = new ArrayList<>();
        multipleEncounterRow.encounterRows.add(anEncounter);

        Messages errorMessages = encounterPersister.persist(multipleEncounterRow);
        assertTrue("Should have persisted the encounters.", errorMessages.isEmpty());
        Context.openSession();
        Context.authenticate("admin", "test");

        List<Encounter> encounters = encounterService.getEncountersByPatientIdentifier(multipleEncounterRow.patientIdentifier);
        Context.closeSession();
        assertEquals(1, encounters.size());
    }

    @Test
    public void persist_case_insensitive_coded_concept_values() {
        MultipleEncounterRow multipleEncounterRow = new MultipleEncounterRow();
        multipleEncounterRow.encounterType = "Consultation";
        multipleEncounterRow.visitType = "OPD";
        multipleEncounterRow.patientIdentifier = "GAN200000";

        EncounterRow anEncounter = new EncounterRow();
        anEncounter.obsRows = new ArrayList<>();
        anEncounter.obsRows.add(new KeyValue("Diagnosis Certainty", "ConFirmeD"));
        anEncounter.encounterDateTime = "1111-11-11";
        anEncounter.diagnosesRows = new ArrayList<>();

        multipleEncounterRow.encounterRows = new ArrayList<>();
        multipleEncounterRow.encounterRows.add(anEncounter);

        encounterPersister.persist(multipleEncounterRow);

        Context.openSession();
        Context.authenticate("admin", "test");
        List<Encounter> encounters = encounterService.getEncountersByPatientIdentifier(multipleEncounterRow.patientIdentifier);
        Context.closeSession();

        Set<Obs> allObs = encounters.get(0).getAllObs();
        assertEquals(407, allObs.iterator().next().getValueCoded().getId().intValue());
    }

    private List<KeyValue> getPatientAttributes() {
        List<KeyValue> patientAttributes = new ArrayList<>();
        patientAttributes.add(new KeyValue("given_name", "Anad"));
        return patientAttributes;
    }
}